package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.json.JsonToDevices;
import controller.json.JsonToLinks;
import controller.json.JsonToSwitches;
import controller.json.StaticFlowPusher;
import controller.util.StringUtils;
import model.ActionOutput;
import model.Controller;
import model.Device;
import model.FlowEntry;
import model.IAction;
import model.Instruction;
import model.InstructionApplyActions;
import model.Link;
import model.Match;
import model.Meter;
import model.OFPort;
import model.Policy;
import model.Queue;
import model.Service;
import model.Switch;

public class FloodlightProvider {

	private Controller controller = new Controller();
	private List<Switch> switchs;
	private List<Device> devices;
	private List<Link> links;
	
	
	private List<Policy> policies;
	private List<Service> services;
	private List<Queue> queues;
	private List<Meter> meters;
	
	
	Map<String, Integer> flowNamesNum;
	public static int ruleNum = 0;
	
	public Map<String ,String []> blackList = new HashMap<>();
	public Map<String ,String []> tasks = new HashMap<>();
	
	private static Logger log = LoggerFactory.getLogger(FloodlightProvider.class);
	
	private static FloodlightProvider INSTANCE;
	public static FloodlightProvider getSingleton(){
		if (INSTANCE == null) {
			INSTANCE = new FloodlightProvider();
		}
		return INSTANCE;
	}
	private FloodlightProvider(){
		flowNamesNum = new HashMap<>();
	}
	
	public Controller getController(){	
		controller.setIP("127.0.0.1");
		controller.setOpenFlowPort(8080);
		return controller;
	}
	
	public List<Device> getDevices(boolean update){
		if (update) {
			try {
				devices = JsonToDevices.getDevices();
			} catch (IOException e) {
				log.error("Failed to get Devices information: {}",e.getMessage());
				//e.printStackTrace();
				return null;
			}
			return devices;
		}else {
			return devices;
		}
	}
	
	public List<Switch> getSwitches(boolean update){
		if (update) {
			try {
				switchs = JsonToSwitches.getSwitches();
				links = JsonToLinks.getLinks();
				for(Link l : links){
					for(Switch s : switchs){
						if(s.getDpid().equals(l.getSrcSwitch())){
							s.links.add(l);
						}
						if(s.getDpid().equals(l.getDstSwtich())){
							Link l1 = new Link();
							l1.setDirection(l.getDirection());
							l1.setType(l.getType());
							l1.setDstPort(l.getSrcPort());
							l1.setDstSwtich(l.getSrcSwitch());
							l1.setSrcPort(l.getDstPort());
							l1.setSrcSwitch(l.getDstSwtich());
							s.links.add(l1);
						}
					}
				}
				
				
			} catch (Exception e) {
				log.error("Failed to get Switches information: {}",e.getMessage());
				//e.printStackTrace();
				return null;
			}
			return switchs;
		}
		else {
			return switchs;
		}
	}
	
	public List<Link> getLinks(boolean update){
		if (update) {
			try {
				links = JsonToLinks.getLinks();
				
			} catch (IOException e) {
				log.error("Failed to get Links information: {}",e.getMessage());
				//e.printStackTrace();
				return null;
			}
			return links;
		}
		else {
			return links;
		}
	}
	
	public void setPath(String srcIp, String dstIp, List<String> swDpids){
		int outPort=0;
		Device ddst = null;
		for(String s : swDpids){
			if(!flowNamesNum.containsKey(s)){
				flowNamesNum.put(s, 0);
			}
		}
		
		for(Device d: devices){
			if(d.getIpv4_addr().equals(srcIp)){
			}
			if(d.getIpv4_addr().equals(dstIp)){
				ddst = d;
			}
		}
		for(int i=0; i<swDpids.size(); ++i){
			Switch cursw = null;
			for(Switch s : switchs){
				if(s.getDpid().equals(swDpids.get(i))){
					cursw = s;
					break;
				}
			}
			if(i==swDpids.size()-1){
				outPort = ddst.getAttachmentPoint().getPort();
			}
			else{
				for(Link l : cursw.links){
					if(l.getDstSwtich().equals(swDpids.get(i+1))){
						outPort = l.getSrcPort();
					}
				}
			}
			FlowEntry e = new FlowEntry();
			e.setActive(true);
			e.setSw(cursw.getDpid());
			int index = flowNamesNum.get(cursw.getDpid());
			e.setName("flow_"+cursw.getDpid()+"_"
					+index);
			flowNamesNum.put(cursw.getDpid(), ++index);
			e.setPriority(32767);
			
			Match match = new Match();
			match.setEthType((short)(0x800));
			match.setIpv4Src(srcIp);
			match.setIpv4Dst(dstIp);
			
			Instruction instruction = new InstructionApplyActions();
			IAction action = new ActionOutput();
			OFPort port = OFPort.of(outPort);
			action.setPort(port);
			List<IAction> actions = new ArrayList<>();
			actions.add(action);
			instruction.setActions(actions);
			List<Instruction> instructions = new ArrayList<>();
			instructions.add(instruction);
			
			e.setMatch(match);
			e.setInstructions(instructions);
			try {
				StaticFlowPusher.addFlow(e);
			} catch (IOException e1) {
				log.error("set path failed");
				e1.printStackTrace();
			}
		}
		
		
		
	}
	public List<String> getPath(String src, String dst)
	{
		List<String> path = new ArrayList<>();
		try {
			List<FlowEntry> flowEntries = StaticFlowPusher.getFlows();
			/*for (int i = 0; i < flowEntries.size(); i++) {
				if (flowEntries.get(i).getMatch().getIpv4Src().equals(src) && flowEntries.get(i).getMatch().getIpv4Dst().equals(dst)) {
					path.add(StringUtils.strip(flowEntries.get(i).getSw(), "\""));
				}
			}*/
			Device dsrc=null, ddst=null;
			for(Device d: devices){
				if(d.getIpv4_addr().equals(src)){
					dsrc = d;
				}
				if(d.getIpv4_addr().equals(dst)){
					ddst = d;
				}
			}
			String sFromId = dsrc.getAttachmentPoint().getSwitchDPID();
			path.add(StringUtils.strip(dsrc.getMac_addr(), "\""));
			boolean isReach = false;
			while(!isReach){
				Switch snow = null;
				for(Switch s: switchs){
					if(s.getDpid().equals(sFromId)){
						snow = s;
						break;
					}
				}
				//已经找到路径
				if(ddst.getAttachmentPoint().getSwitchDPID().equals(sFromId)){
					isReach = true;
					path.add(StringUtils.strip(sFromId, "\""));
					path.add(ddst.getMac_addr());
					break;
				}
				for(FlowEntry fe : flowEntries){
					String rowSw = fe.getSw();
					rowSw = rowSw.substring(1, rowSw.length()-1);
					if(rowSw.equals(sFromId)){
						//ipv4源，目的匹配或者源为空，目的匹配
						int portNumber;
						if(fe.getMatch().getIpv4Src()==null){
							 if(fe.getMatch().getIpv4Dst().equals(dst)){
								 portNumber = fe.getInstructions().get(0).getActions()
										 .get(0).getPort().getPortNumber();
								 for(Link l : snow.links){
									 if(l.getSrcPort()==portNumber){
										 path.add(StringUtils.strip(sFromId, "\""));
										 sFromId = l.getDstSwtich();
									 }
								 }
							 }
						}
						else{
							if(fe.getMatch().getIpv4Src().equals(src) && 
									fe.getMatch().getIpv4Dst().equals(dst)){
								portNumber = fe.getInstructions().get(0).getActions()
										.get(0).getPort().getPortNumber();
								for(Link l : snow.links){
									 if(l.getSrcPort()==portNumber){
										 path.add(StringUtils.strip(sFromId, "\""));
										 sFromId = l.getDstSwtich();
									 }
								 }
							}
						}
					}
					
				}
			}
			
		} catch (IOException e) {
			log.error("Failed to get Flows from Controller's StaticFlowPusher module: {}",e.getMessage());
			return path;
			//e.printStackTrace();
		}
		
		return path;
	}
	
	public void setPolicies(List<Policy> policies){
		this.policies = policies;
	}
	
	public List<Policy> getPolicies(){
		return this.policies;
	}
	
	public void setServices(List<Service> services){
		this.services = services;
	}
	
	public List<Service> getServices(){
		return this.services;
	}
	
	public void setQueues(List<Queue> queues){
		this.queues = queues;
	}
	
	public List<Queue> getQueues(){
		return this.queues;
	}
	
	public void setMeters(List<Meter> meters){
		this.meters = meters;
	}
	
	public List<Meter> getMeters(){
		return this.meters;
	}
}
