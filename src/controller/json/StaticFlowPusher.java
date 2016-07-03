package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import controller.FloodlightProvider;
import controller.util.HTTPUtil;
import controller.util.StringUtils;
import model.ActionOutput;
import model.FlowEntry;
import model.IAction;
import model.Instruction;
import model.InstructionApplyActions;
import model.Match;
import model.OFPort;

public class StaticFlowPusher {
	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static Future<Object> futurelist,futureadd,futuredelete;
	
	private static Logger log = LoggerFactory.getLogger(StaticFlowPusher.class);
	public static ArrayList<FlowEntry> getFlows()throws IOException{
		return getFlows("all");
	}
	public static ArrayList<FlowEntry> getFlows(String sw)throws IOException{
		
		ArrayList<FlowEntry> flowEntries = new ArrayList<FlowEntry>();
		
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return flowEntries;
		}
		String listurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/staticflowpusher/list/" + sw + "/json";
		futurelist = HTTPUtil.getJsonFromURL(listurl);
		
		try {
			String jsonFlows = (String) futurelist.get(5, TimeUnit.SECONDS);
			jsonFlows = StringUtils.strip(jsonFlows, "{}");
			jsonFlows = jsonFlows.replaceAll("],\"", "]|\"");
			String [] swFlows = jsonFlows.split("\\|");
			
			for (int i=0;i<swFlows.length;i++) {
				swFlows[i] = swFlows[i].replaceAll("\"\\:\\[", "\"|[");
				String[] sflow  = swFlows[i].split("\\|");
				if(sflow.length == 2)
				{
					String switchx = sflow[0];
					sflow[1] = StringUtils.strip(sflow[1], "[]");
					sflow[1] = sflow[1].replaceAll("},[{]", "}|{");
					String[] flows = sflow[1].split("\\|");
					for (int j = 0; j < flows.length; j++) {
						if (jsonToFlow(flows[j],switchx)!=null) {
							flowEntries.add(jsonToFlow(flows[j], switchx));
							log.info("Get flow entry: {}", jsonToFlow(flows[j], switchx));
						}
					}
				}
			}
		} catch (InterruptedException e) {
			log.error("Failed to get Flow Table information from Controller's module StaticFlowPusher: {}", e.getMessage());
			return flowEntries;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get Flow Table information from Controller's module StaticFlowPusher: {}", e.getMessage());
			return flowEntries;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get Flow Table information from Controller's module StaticFlowPusher: {}", e.getMessage());
			return flowEntries;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to get Flow Table information from Controller's module StaticFlowPusher: {}", e.getMessage());
			return flowEntries;
			//e.printStackTrace();
		}
		
		return flowEntries;
	}
	
	public static boolean addFlow(FlowEntry flowEntry) throws IOException{

		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		
		String addurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/staticflowpusher/json";
		
		String json = flowEntry.toJson();
		log.info("Add flow entry of JSON: {}", json);
		futureadd = HTTPUtil.postJsonToURL(addurl, json);
		try {
			String jsonRespond = (String) futureadd.get(5, TimeUnit.SECONDS);
			System.out.println(jsonRespond);
			if (jsonRespond != "") {
				return true;
			}else {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to add flow entry: {}", e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to add flow entry: {}", e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to add flow entry: {}", e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to add flow entry: {}", e.getMessage());
			return false;
			//e.printStackTrace();
		}
	}
	
	public static boolean deleteFlow(String name) throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/staticflowpusher/json";
		
		String json = "{\"name\":\"" + name + "\"}";
		log.info("Delete flow entry name: {}",json);
		futuredelete = HTTPUtil.deleteJsonToURL(url, json);
		try {
			String jsonRespond = (String) futuredelete.get(5, TimeUnit.SECONDS);
			if (jsonRespond != "") {
				return true;
			}else {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to delete flow entry {}: {}",name, e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to delete flow entry {}: {}",name, e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to delete flow entry {}: {}",name, e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to delete flow entry {}: {}",name, e.getMessage());
			return false;
			//e.printStackTrace();
		}
	}
	
	public static FlowEntry jsonToFlow(String pJson, String sw) throws IOException{
		FlowEntry flowEntry = new FlowEntry();
		flowEntry.setSw(sw);
		MappingJsonFactory jsonFactory = new MappingJsonFactory();
		JsonParser jsonParser;

		try {
			jsonParser = jsonFactory.createJsonParser(pJson);
		} catch (JsonParseException e) {
			throw new IOException(e);
		}
		JsonToken token = jsonParser.getCurrentToken();
		if (token != JsonToken.START_OBJECT) {
			jsonParser.nextToken();
			if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
				log.error("Did not recieve json start token, current " +
    					"token is: " + jsonParser.getCurrentToken());
				return null;
			}
		}
		jsonParser.nextToken();
		flowEntry.setName(jsonParser.getCurrentName());
		if (token != JsonToken.START_OBJECT) {
			jsonParser.nextToken();
			if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
				log.error("Did not recieve json start token, current " +
    					"token is: " + jsonParser.getCurrentToken());
				return null;
			}
		}
		while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
			if (jsonParser.getCurrentToken() != JsonToken.FIELD_NAME) {
				throw new IOException("FIELD_NAME expected");
			}
			
			try {
				String fieldName = jsonParser.getCurrentName();
				jsonParser.nextToken();
				if (jsonParser.getText().equals("")) {
					continue;
				}
				switch (fieldName) {
				case "version":
					flowEntry.setVersion(jsonParser.getText());
					break;
				case "command":
					flowEntry.setCommand(jsonParser.getText());
					break;
				case "cookie":
					flowEntry.setCookie(jsonParser.getText());
					break;
				case "priority":
					flowEntry.setPriority(Integer.parseInt(jsonParser.getText()));
					break;
				case "match":
					Match match = new Match();
					if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
						while (jsonParser.nextToken()!=JsonToken.END_OBJECT) {
							if (jsonParser.getCurrentToken() != JsonToken.FIELD_NAME) {
								throw new IOException("FIELD_NAME expected");
							}
							String field = jsonParser.getCurrentName();
							jsonParser.nextToken();
							switch (field) {
							case "in_port":
								match.setInPort(OFPort.of(Integer.parseInt(jsonParser.getText())));
								break;
							case "eth_type":
								match.setEthType(Short.parseShort(jsonParser.getText().replace("0x", ""), 16));
								break;
							case "eth_src":
								match.setEthSrc(jsonParser.getText());
								break;
							case "eth_dst":
								match.setEthDst(jsonParser.getText());
								break;
							case "eth_vlan_vid":
								match.setVlanVid(Short.parseShort(jsonParser.getText()));
								break;
							case "ip_proto":
								match.setIpProto(Short.parseShort(jsonParser.getText()));
								break;
							case "ipv4_src":
								match.setIpv4Src(jsonParser.getText());
								break;
							case "ipv4_dst":
								match.setIpv4Dst(jsonParser.getText());
								break;
							case "ip_tos":
								match.setIpTos(Integer.parseInt(jsonParser.getText()));
								break;
							case "ip_dscp":
								match.setIpDscp(Integer.parseInt(jsonParser.getText()));
								break;
							case "udp_src":
								match.setUdpSrc(Integer.parseInt(jsonParser.getText()));
								break;
							case "udp_dst":
								match.setUdpDst(Integer.parseInt(jsonParser.getText()));
								break;
							case "tcp_src":
								match.setTcpSrc(Integer.parseInt(jsonParser.getText()));
								break;
							case "tcp_dst":
								match.setTcpDst(Integer.parseInt(jsonParser.getText()));
								break;
							default:
								log.info("[JSON PARSER]Match field error," + fieldName);
								break;
							}
						}
					}
					flowEntry.setMatch(match);
					break;
				case "idleTimeoutSec":
					flowEntry.setIdleTimeout(Integer.parseInt(jsonParser.getText()));
					break;
				case "hardTimeoutSec":
					flowEntry.setHardTimeout(Integer.parseInt(jsonParser.getText()));
					break;
				case "outPort":
					flowEntry.setOutPort(OFPort.of(jsonParser.getText()));
					break;
				case "outGroup":
					flowEntry.setOutGroup(jsonParser.getText());
					break;
				case "flags":
					flowEntry.setFlags(jsonParser.getText());
					break;
				case "cookieMask":
					flowEntry.setCookieMask(jsonParser.getText());
					break;
				case "instructions":
					List<Instruction> instructions = new ArrayList<Instruction>();
					if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
						while (jsonParser.nextToken()!=JsonToken.END_OBJECT) {
							if (jsonParser.getCurrentToken() != JsonToken.FIELD_NAME) {
								throw new IOException("FIELD_NAME expected");
							}
							String instructionType = jsonParser.getCurrentName();
							jsonParser.nextToken();
							switch (instructionType) {
							case "instruction_apply_actions":
								Instruction instruction = new InstructionApplyActions();
								List<IAction> actions = new ArrayList<IAction>();
								if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
									while (jsonParser.nextToken()!=JsonToken.END_OBJECT) {
										if (jsonParser.getCurrentToken() != JsonToken.FIELD_NAME) {
											throw new IOException("FIELD_NAME expected");
										}
										String actionsKey = jsonParser.getCurrentName();
										jsonParser.nextToken();
										if (actionsKey == "actions") {
											String actionsVal = jsonParser.getText();
											String[] actionStr = actionsVal.split("\\,");
											for (int i = 0; i < actionStr.length; i++) {
												String[] tmp = actionStr[i].split("\\=");
												switch (tmp[0]) {
												case "output":
													ActionOutput action = new ActionOutput();
													switch (tmp[1]) {
													case "all":
														action.setPort(OFPort.ALL);
														break;
													case "controller":
														action.setPort(OFPort.CONTROLLER);
														break;
													case "local":
														action.setPort(OFPort.LOCAL);
														break;
													case "in_port":
														action.setPort(OFPort.IN_PORT);
														break;
													case "normal":
														action.setPort(OFPort.NORMAL);
														break;
													case "flood":
														action.setPort(OFPort.FLOOD);
														break;
													default:
														action.setPort(OFPort.of(Integer.parseInt(tmp[1])));
														break;
													}
													actions.add(action);
													break;
												case "group":
													
													break;
												case "set_queue":
													
													break;
												case "set_eth_src":
													
													break;
												case "set_eth_dst":
													
													break;
												case "set_ipv4_src":
													
													break;
												case "set_ipv4_dst":
													
													break;
												case "set_field":
													
													break;
												default:
													break;
												}
											}
										}
									}
								}
								instruction.setActions(actions);
								instructions.add(instruction);
								break;
							case "instruction_write_actions":
								
								break;
							case "instruction_clear_actions":
								
								break;
							case "instruction_write_metadata":
								
								break;
							case "instruction_goto_table":
								
								break;
							case "instruction_goto_meter":
								
								break;
							default:
								log.info("[JSON PARSER]Instruction field error," + fieldName);
								break;
							}
						}
					}
					flowEntry.setInstructions(instructions);
					break;
				default:
					log.info("[JSON PARSER]FlowEntry field error," + fieldName);
					return null;
				}
			} catch (JsonParseException e) {
				log.error("Error getting current FIELD_NAME {}", e.getMessage());
				return null;
				//e.printStackTrace();
			}catch (IOException e) {
				log.error("Error procession Json {}", e);
				return null;
				//e.printStackTrace();
			}
		}
		return flowEntry;
	}
}
