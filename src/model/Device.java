package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Device {
	protected String mac_addr;
	protected String ipv4_addr;
	protected int vlan;
	protected AttachmentPoint attachmentPoint;
	protected Date lastSeen;
	public List<Link> links;
	
	public Device(String mac_addr){
		this.mac_addr = mac_addr;
		this.ipv4_addr = null;
		this.vlan = -1;
		this.attachmentPoint = null;
		this.lastSeen = null;
		this.links = new ArrayList<>();
	}
	public String getMac_addr() {
		return mac_addr;
	}
	public void setMac_addr(String mac_addr) {
		this.mac_addr = mac_addr;
	}
	public String getIpv4_addr() {
		return ipv4_addr;
	}
	public void setIpv4_addr(String ipv4_addr) {
		this.ipv4_addr = ipv4_addr;
	}
	public int getVlan() {
		return vlan;
	}
	public void setVlan(int vlan) {
		this.vlan = vlan;
	}
	public AttachmentPoint getAttachmentPoint() {
		return attachmentPoint;
	}
	public void setAttachmentPoint(AttachmentPoint attachmentPoint) {
		this.attachmentPoint = attachmentPoint;
	}
	public Date getLastSeen() {
		return lastSeen;
	}
	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}
	@Override
	public String toString() {
		return "Device [mac_addr=" + mac_addr + ", ipv4_addr=" + ipv4_addr
				+ ", vlan=" + vlan + ", attachmentPoint=" + attachmentPoint
				+ ", lastSeen=" + lastSeen + "]";
	}
}
