package model;

import java.util.ArrayList;
import java.util.List;

public class Switch {
	protected String mfr_desc;
	protected String hw_desc;
	protected String sw_desc;
	protected String serial_num;
	protected String dp_desc;
	protected String dpid;
	protected long packet_count;
	protected long byte_count;
	protected int flow_count;
	protected List<Port> ports;
	protected String capabilities;
	protected int buffers;
	protected int tables;
	public List<Link> links;
	
	public Switch(String dpid){
		this.dpid = dpid;
		this.mfr_desc = null;
		this.hw_desc = null;
		this.sw_desc = null;
		this.serial_num = null;
		this.dp_desc = null;
		this.packet_count = -1;
		this.byte_count = -1;
		this.ports = null;
		this.links = new ArrayList<>();
		capabilities = null;
		buffers = -1;
		tables = -1;
	}
	
	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	public int getBuffers() {
		return buffers;
	}

	public void setBuffers(int buffers) {
		this.buffers = buffers;
	}

	public int getTables() {
		return tables;
	}

	public void setTables(int tables) {
		this.tables = tables;
	}
	
	public String getMfr_desc() {
		return mfr_desc;
	}
	public void setMfr_desc(String mfr_desc) {
		this.mfr_desc = mfr_desc;
	}
	public String getHw_desc() {
		return hw_desc;
	}
	public void setHw_desc(String hw_desc) {
		this.hw_desc = hw_desc;
	}
	public String getSw_desc() {
		return sw_desc;
	}
	public void setSw_desc(String sw_desc) {
		this.sw_desc = sw_desc;
	}
	public String getSerial_num() {
		return serial_num;
	}
	public void setSerial_num(String serial_num) {
		this.serial_num = serial_num;
	}
	public String getDp_desc() {
		return dp_desc;
	}
	public void setDp_desc(String dp_desc) {
		this.dp_desc = dp_desc;
	}
	public String getDpid() {
		return dpid;
	}
	public void setDpid(String dpid) {
		this.dpid = dpid;
	}
	public long getPacket_count() {
		return packet_count;
	}
	public void setPacket_count(long packet_count) {
		this.packet_count = packet_count;
	}
	public long getByte_count() {
		return byte_count;
	}
	public void setByte_count(long byte_count) {
		this.byte_count = byte_count;
	}
	public int getFlow_count() {
		return flow_count;
	}
	public void setFlow_count(int flow_count) {
		this.flow_count = flow_count;
	}
	public List<Port> getPorts() {
		return ports;
	}
	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}
	
	
}
