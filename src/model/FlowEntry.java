package model;

import java.util.List;

public class FlowEntry {
	private String name = null;
	private String sw = null;
	private String version = null;
	private String command = null;
	private String cookie = null;
    private String cookieMask = null;
    private int tableId = -1;
    private int idleTimeout = -1;
    private int hardTimeout = -1;
    private int priority = 32767;
    private int bufferId = -1;
    private OFPort outPort = OFPort.ANY;
    private String outGroup = null;
    private String flags = null;
    private Match match;
    private List<Instruction> instructions;
    
    boolean active = true;

	long packetCount = -1;
    long byteCount = -1;
    long durationSec = -1;
    long durationNsec = -1;
    
	public String toJson()
	{
		String result = "{";
		if(name != null)
		{
			result += "\"name\":\"" + name + "\",";
			if(sw != null)
			{
				result += "\"switch\":\"" + sw + "\",";
				if(cookie != null)
					result += "\"cookie\":" + cookie + "\",";
				if(cookieMask != null)
					result += "\"cookie_mask\":\"" + cookieMask + "\",";
				if(tableId != -1)
					result += "\"table\":\"" + tableId + "\",";
				if(idleTimeout != -1)
					result += "\"idle_timeout\":\"" + idleTimeout + "\",";
				if(hardTimeout != -1)
					result += "\"hard_timeout\":\"" + hardTimeout + "\",";
				//if(priority != -1)
				result += "\"priority\":\"" + priority + "\",";
//				else
//					return "{}";
				result += "\"active\":\"" + active + "\",";
				
				
				
				//match
				if(match.getInPort() != OFPort.ZERO)
					result += "\"in_port\":\"" + match.getInPort().getPortNumber() + "\",";
				if(match.getEthType() != -1)
					result += "\"eth_type\":\"" + match.getEthType() + "\",";
				if(match.getEthSrc() != null)
					result += "\"eth_src\":\"" + match.getEthSrc() + "\",";
				if(match.getEthDst() != null)
					result += "\"eth_dst\":\"" + match.getEthDst() + "\",";
				if(match.getVlanVid() != -1)
					result += "\"eth_vlan_vid\":\"" + match.getVlanVid() + "\",";
				if(match.getIpProto() != -1)
					result += "\"ip_proto\":\"" + match.getIpProto() + "\",";
				if(match.getIpv4Src() != null)
					result += "\"ipv4_src\":\"" + match.getIpv4Src() + "\",";
				if(match.getIpv4Dst() != null)
					result += "\"ipv4_dst\":\"" + match.getIpv4Dst() + "\",";
				if(match.getIpTos() != -1)
					result += "\"ip_tos\":\"" + match.getIpTos() + "\",";
				if(match.getIpDscp() != -1)
					result += "\"ip_dscp\":\"" + match.getIpDscp() + "\",";
				if(match.getUdpSrc() != -1)
					result += "\"udp_src\":\"" + match.getUdpSrc() + "\",";
				if(match.getUdpDst() != -1)
					result += "\"udp_dst\":\"" + match.getUdpDst() + "\",";
				if(match.getTcpSrc() != -1)
					result += "\"tcp_src\":\"" + match.getTcpSrc() + "\",";
				if(match.getTcpDst() != -1)
					result += "\"tcp_dst\":\"" + match.getTcpDst() + "\",";
				
				
				for (int i = 0; i < instructions.size(); i++) {
					switch (instructions.get(i).getType()) {
					case APPLY_ACTIONS:
						result += "\"instruction_apply_actions\":\"";
						List<IAction> actions = instructions.get(i).getActions();
						for (int j = 0; j < actions.size(); j++) {
							switch (actions.get(j).getType()) {
							case OUTPUT:
								ActionOutput actionOutput = (ActionOutput)actions.get(j);
								if (actionOutput.getPort() == OFPort.ALL)
									result += "output=all";
								else if (actionOutput.getPort() == OFPort.CONTROLLER)
									result += "output=controller";
								else if (actionOutput.getPort() == OFPort.FLOOD)
									result += "output=flood";
								else if (actionOutput.getPort() == OFPort.IN_PORT)
									result += "output=ingress-port";
								else if (actionOutput.getPort() == OFPort.LOCAL)
									result += "output=local ";
								else if (actionOutput.getPort() == OFPort.NORMAL)
									result += "output=normal";
								else
									result += "output=" + actionOutput.getPort().getPortNumber();
								break;
							case SET_VLAN_VID:
								
								break;
							case SET_DL_SRC:
								
								break;
							case SET_DL_DST:
								
								break;
							case SET_NW_SRC:
								
								break;
							case SET_NW_DST:
								
								break;
								
//OUTPUT,SET_VLAN_VID,SET_VLAN_PCP,STRIP_VLAN,SET_DL_SRC,SET_DL_DST,SET_NW_SRC,
//SET_NW_DST,SET_NW_TOS,SET_TP_SRC,SET_TP_DST,ENQUEUE,EXPERIMENTER,SET_NW_ECN,
//COPY_TTL_OUT,COPY_TTL_IN,SET_MPLS_LABEL,SET_MPLS_TC,SET_MPLS_TTL,DEC_MPLS_TTL,
//PUSH_VLAN,POP_VLAN,PUSH_MPLS,POP_MPLS,SET_QUEUE,GROUP,SET_NW_TTL,DEC_NW_TTL,
//SET_FIELD,PUSH_PBB,POP_PBB;
							default:
								break;
							}
						if(j != (actions.size() -1))
								result += ",";
						}
						result += "\"";
						break;
					case GOTO_TABLE:
						
						break;
					case WRITE_METADATA:
						
						break;
					case WRITE_ACTIONS:
						
						break;
					case CLEAR_ACTIONS:
						
						break;
					case EXPERIMENTER:
							break;
					case METER:
							break;
					default:
						break;
					}
					if(i != (instructions.size() -1))
						result += ",";
				}
			}
			else
				return "{}";
		}
		else
			return "{}";
		return result + "}";
	}
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("FlowEntry(");
        b.append("name=").append(name);
        b.append(", "); 
        b.append("switch=").append(sw);
        b.append(", ");
        b.append("command=").append(command);
        b.append(", ");
        b.append("active=").append(active);
        b.append(", ");
        b.append("cookie=").append(cookie);
        b.append(", ");
        b.append("cookieMask=").append(cookieMask);
        b.append(", ");
        b.append("tableId=").append(tableId);
        b.append(", ");
        b.append("idleTimeout=").append(idleTimeout);
        b.append(", ");
        b.append("hardTimeout=").append(hardTimeout);
        b.append(", ");
        b.append("priority=").append(priority);
        b.append(", ");
        b.append("bufferId=").append(bufferId);
        b.append(", ");
        b.append("outPort=").append(outPort);
        b.append(", ");
        b.append("outGroup=").append(outGroup);
        b.append(", ");
        b.append("flags=").append(flags);
        b.append(", ");
        b.append("match=").append(match);
        b.append(", ");
        b.append("instructions=").append(instructions);
        b.append(")");
        return b.toString();
    }

    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getCookieMask() {
		return cookieMask;
	}

	public void setCookieMask(String cookieMask) {
		this.cookieMask = cookieMask;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public int getHardTimeout() {
		return hardTimeout;
	}

	public void setHardTimeout(int hardTimeout) {
		this.hardTimeout = hardTimeout;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getBufferId() {
		return bufferId;
	}

	public void setBufferId(int bufferId) {
		this.bufferId = bufferId;
	}

	public OFPort getOutPort() {
		return outPort;
	}

	public void setOutPort(OFPort outPort) {
		this.outPort = outPort;
	}

	public String getOutGroup() {
		return outGroup;
	}

	public void setOutGroup(String outGroup) {
		this.outGroup = outGroup;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<Instruction> instructions) {
		this.instructions = instructions;
	}

	public long getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(long packetCount) {
		this.packetCount = packetCount;
	}

	public long getByteCount() {
		return byteCount;
	}

	public void setByteCount(long byteCount) {
		this.byteCount = byteCount;
	}

	public long getDurationSec() {
		return durationSec;
	}

	public void setDurationSec(long durationSec) {
		this.durationSec = durationSec;
	}

	public long getDurationNsec() {
		return durationNsec;
	}

	public void setDurationNsec(long durationNsec) {
		this.durationNsec = durationNsec;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSw() {
		return sw;
	}

	public void setSw(String sw) {
		this.sw = sw;
	}
}
