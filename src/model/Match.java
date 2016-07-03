package model;

public class Match {
	private OFPort in_port = OFPort.ZERO;
	private short eth_type = -1;
	private String eth_src = null;
	private String eth_dst = null;
	private short vlan_vid = -1;
	//private short vlan_pcp;
	private short ip_proto = -1;
	private String ipv4_src = null;
	private String ipv4_dst = null;
//	private String ipv6_src;
//	private String ipv6_dst;
//	private int ipv6_label;
	private int ip_tos = -1;
	private int ip_ecn = -1;
	private int ip_dscp = -1;
//	private tp_src
//	private tp_dst
	private int udp_src = -1;
	private int udp_dst = -1;
	private int tcp_src = -1;
	private int tcp_dst = -1;

	@Override
    public String toString() {
        StringBuilder b = new StringBuilder("Match(");
        b.append("in_port=").append(in_port);
        b.append(", "); 
        b.append("eth_type=").append(eth_type);
        b.append(", ");
        b.append("eth_src=").append(eth_src);
        b.append(", ");
        b.append("eth_dst=").append(eth_dst);
        b.append(", ");
        b.append("vlan_vid=").append(vlan_vid);
        b.append(", ");
        b.append("ip_proto=").append(ip_proto);
        b.append(", ");
        b.append("ipv4_src=").append(ipv4_src);
        b.append(", ");
        b.append("ipv4_dst=").append(ipv4_dst);
        b.append(", ");
//        b.append("hardTimeout=").append(ipv6_src);
//        b.append(", ");
//        b.append("priority=").append(ipv6_dst);
//        b.append(", ");
//        b.append("bufferId=").append(ipv6_label);
//        b.append(", ");
        b.append("ip_tos=").append(ip_tos);
        b.append(", ");
        b.append("ip_ecn=").append(ip_ecn);
        b.append(", ");
        b.append("ip_dscp=").append(ip_dscp);
        b.append(", ");
        b.append("udp_src=").append(udp_src);
        b.append(", ");
        b.append("udp_dst=").append(udp_dst);
        b.append(", ");
        b.append("tcp_src=").append(tcp_src);
        b.append(", ");
        b.append("tcp_dst=").append(tcp_dst);
        b.append(")");
        return b.toString();
    }

	public OFPort getInPort() {
		return in_port;
	}
	public void setInPort(OFPort in_port) {
		this.in_port = in_port;
	}
	public short getEthType() {
		return eth_type;
	}
	public void setEthType(short eth_type) {
		this.eth_type = eth_type;
	}
	public String getEthSrc() {
		return eth_src;
	}
	public void setEthSrc(String eth_src) {
		this.eth_src = eth_src;
	}
	public String getEthDst() {
		return eth_dst;
	}
	public void setEthDst(String eth_dst) {
		this.eth_dst = eth_dst;
	}
	public short getVlanVid() {
		return vlan_vid;
	}
	public void setVlanVid(short vlan_vid) {
		this.vlan_vid = vlan_vid;
	}
	public short getIpProto() {
		return ip_proto;
	}
	public void setIpProto(short ip_proto) {
		this.ip_proto = ip_proto;
	}
	public String getIpv4Src() {
		return ipv4_src;
	}
	public void setIpv4Src(String ipv4_src) {
		this.ipv4_src = ipv4_src;
	}
	public String getIpv4Dst() {
		return ipv4_dst;
	}
	public void setIpv4Dst(String ipv4_dst) {
		this.ipv4_dst = ipv4_dst;
	}
	public int getIpTos() {
		return ip_tos;
	}
	public void setIpTos(int ip_tos) {
		this.ip_tos = ip_tos;
	}
	public int getIpEcn() {
		return ip_ecn;
	}
	public void setIpEcn(int ip_ecn) {
		this.ip_ecn = ip_ecn;
	}
	public int getIpDscp() {
		return ip_dscp;
	}
	public void setIpDscp(int ip_dscp) {
		this.ip_dscp = ip_dscp;
	}
	public int getUdpSrc() {
		return udp_src;
	}
	public void setUdpSrc(int udp_src) {
		this.udp_src = udp_src;
	}
	public int getUdpDst() {
		return udp_dst;
	}
	public void setUdpDst(int udp_dst) {
		this.udp_dst = udp_dst;
	}
	public int getTcpSrc() {
		return tcp_src;
	}
	public void setTcpSrc(int tcp_src) {
		this.tcp_src = tcp_src;
	}
	public int getTcpDst() {
		return tcp_dst;
	}
	public void setTcpDst(int tcp_dst) {
		this.tcp_dst = tcp_dst;
	}
	
	
//	private sctp_src
//	private sctp_dst
//	private int icmpv4_type;
//	private int icmpv4_code;
//	private int icmpv6_type;
//	private int icmpv6_code;
//	private String ipv6_nd_ssl;
//	private String ipv6_nd_ttl;
//	private String ipv6_nd_target;
//	private int arp_opcode;
//	private String arp_sha;
//	private String arp_tha;
//	private String arp_spa;
//	private String arp_tpa;
//	private mpls_label
//	private mpls_tc
//	private mpls_bos
//	private metadata
}
