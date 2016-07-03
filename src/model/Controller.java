package model;

import java.util.List;

public class Controller {

	protected String IP;
	protected int openFlowPort;
	protected List<String> loadedModules;
	protected List<String> allModules;
	protected String role;
	protected List<String> tables;
	protected String health;
	protected Memory memory;
	protected long uptime;
	protected String qosStatus;
	
	public Controller() {
		this.IP = null;
		this.openFlowPort = -1;
		this.loadedModules = null;
		this.allModules = null;
		this.role = null;
		this.tables = null;
		this.health = null;
		this.memory = null;
		this.uptime = -1;
		this.qosStatus = "No";
	}
	
	public Controller(String IP,int openFlowPort) {
		this.IP = IP;
		this.openFlowPort = openFlowPort;
		this.loadedModules = null;
		this.allModules = null;
		this.role = null;
		this.tables = null;
		this.health = null;
		this.memory = null;
		this.uptime = -1;
		this.qosStatus = "No";
	}
	
	public Controller( String IP,int openFlowPort,
			List<String> loadedModules,List<String> allModules,
			String role,List<String> tables,
			String health,Memory memory,long uptime){
		this.IP = IP;
		this.openFlowPort = openFlowPort;
		this.loadedModules = loadedModules;
		this.allModules = allModules;
		this.role = role;
		this.tables = tables;
		this.health = health;
		this.memory = memory;
		this.uptime = uptime;
	}
	
	public String getIP(){
		return this.IP;
	}
	
	public int getOpenFlowPort(){
		return this.openFlowPort;
	}
	
	public List<String> getLoadedModules(){
		return this.loadedModules;
	}
	
	public List<String> getAllModules(){
		return this.allModules;
	}
	
	public String getRole(){
		return this.role;
	}
	
	public List<String> getTables(){
		return this.tables;
	}
	public String getHealth(){
		return this.health;
	}
	
	public Memory getMemory(){
		return this.memory;
	}
	public long getUptime(){
		return this.uptime;
	}
	
	public void setIP(String ip){
		this.IP = ip;
	}
	
	public void setOpenFlowPort(int port){
		this.openFlowPort = port;
	}
	
	public void setLoadedModules(List<String> modules){
		this.loadedModules = modules;
	}
	
	public void setAllModules(List<String> modules){
		this.allModules = modules;
	}
	
	public void setRole(String role){
		this.role = role;
	}
	
	public void setTables(List<String> tables){
		this.tables = tables;
	}
	
	public void setHealth(String health){
		this.health = health;
	}
	
	public void setMemory(Memory memory){
		this.memory = memory;
	}
	
	public void setUptime(long time){
		this.uptime = time;
	}
	
	public void setQoSStatus(String qosStatus){
		this.qosStatus = qosStatus;
	}
	
	public String getQosStatus(){
		return this.qosStatus;
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder("Contoller(");
        b.append("IP=").append(IP);
        b.append(", ");
        b.append("OpenFlowPort=").append(openFlowPort);
        b.append(", ");
        b.append("role=").append(role);
        b.append(", ");
        b.append("health=").append(health);
        b.append(", ");
        b.append("memory=").append(memory.free + "free of" + memory.total);
        b.append(", ");
        b.append("uptime=").append(uptime);
        b.append(", ");
        b.append("QoSStatus=").append(qosStatus);
        b.append(")");
        return b.toString();
	}
}
