package model;

public class Policy {

	protected long pid = -1;
	protected String name = null;
	protected String sw = "all";
	protected short priority = 32767;
	protected IPolicyType policyType = null;
	protected short inport = -1;
	protected String ethsrc = null;
	protected String ethdst = null;
	protected short ethtype = -1;
	protected short vlanid = -1;
	protected String ipsrc = "";
	protected String ipdst = "";
	protected byte protocol = -1;
	protected byte tos = -1;
	protected short srcport = -1;
	protected short dstport = -1;
	protected boolean enable = false;
	
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
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
	public short getPriority() {
		return priority;
	}
	public void setPriority(short priority) {
		this.priority = priority;
	}
	public IPolicyType getPolicyType() {
		return policyType;
	}
	public void setPolicyType(IPolicyType policyType) {
		this.policyType = policyType;
	}
	public short getInport() {
		return inport;
	}
	public void setInport(short inport) {
		this.inport = inport;
	}
	public String getEthsrc() {
		return ethsrc;
	}
	public void setEthsrc(String ethsrc) {
		this.ethsrc = ethsrc;
	}
	public String getEthdst() {
		return ethdst;
	}
	public void setEthdst(String ethdst) {
		this.ethdst = ethdst;
	}
	public short getEthtype() {
		return ethtype;
	}
	public void setEthtype(short ethtype) {
		this.ethtype = ethtype;
	}
	public short getVlanid() {
		return vlanid;
	}
	public void setVlanid(short vlanid) {
		this.vlanid = vlanid;
	}
	public String getIpsrc() {
		return ipsrc;
	}
	public void setIpsrc(String ipsrc) {
		this.ipsrc = ipsrc;
	}
	public String getIpdst() {
		return ipdst;
	}
	public void setIpdst(String ipdst) {
		this.ipdst = ipdst;
	}
	public byte getProtocol() {
		return protocol;
	}
	public void setProtocol(byte protocol) {
		this.protocol = protocol;
	}
	public byte getTos() {
		return tos;
	}
	public void setTos(byte tos) {
		this.tos = tos;
	}
	public short getSrcport() {
		return srcport;
	}
	public void setSrcport(short srcport) {
		this.srcport = srcport;
	}
	public short getDstport() {
		return dstport;
	}
	public void setDstport(short dstport) {
		this.dstport = dstport;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	@Override
	public String toString() {
		return "Policy [pid=" + pid + ", name=" + name + ", sw=" + sw
				+ ", priority=" + priority + ", policyType=" + policyType
				+ ", ingressport=" + inport + ", ethsrc=" + ethsrc
				+ ", ethdst=" + ethdst + ", ethtype=" + ethtype + ", vlanid="
				+ vlanid + ", ipsrc=" + ipsrc + ", ipdst=" + ipdst
				+ ", protocol=" + protocol + ", tos=" + tos + ", srcport="
				+ srcport + ", dstport=" + dstport + ", enable=" + enable + "]";
	}
	
}
