package model;

public class Link {

	protected String srcSwitch;
	protected int srcPort;
	protected String dstSwtich;
	protected int dstPort;
	protected String type;
	protected String direction;

	public String getSrcSwitch() {
		return srcSwitch;
	}
	public void setSrcSwitch(String srcSwitch) {
		this.srcSwitch = srcSwitch;
	}
	public int getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}
	public String getDstSwtich() {
		return dstSwtich;
	}
	public void setDstSwtich(String dstSwtich) {
		this.dstSwtich = dstSwtich;
	}
	public int getDstPort() {
		return dstPort;
	}
	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		return "Link [srcSwitch=" + srcSwitch + ", srcPort=" + srcPort
				+ ", dstSwtich=" + dstSwtich + ", dstPort=" + dstPort
				+ ", type=" + type + ", direction=" + direction + "]";
	}
	
}
