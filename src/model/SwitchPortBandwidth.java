package model;

import java.util.Date;

public class SwitchPortBandwidth {
	private String dpid;
	private OFPort pt;
	private long rx;
	private long tx;
	private Date time;
	
	private SwitchPortBandwidth() {}
	private SwitchPortBandwidth(String d, OFPort p, long rx, long tx) {
		dpid = d;
		pt = p;
		this.rx = rx;
		this.tx = tx;
		time = new Date();
	}
	
	public static SwitchPortBandwidth of(String d, OFPort p, long rx, long tx) {
		if (d == null) {
			throw new IllegalArgumentException("Datapath ID cannot be null");
		}
		if (p == null) {
			throw new IllegalArgumentException("Port cannot be null");
		}
//		if (rx == null) {
//			throw new IllegalArgumentException("RX bandwidth cannot be null");
//		}
//		if (tx == null) {
//			throw new IllegalArgumentException("TX bandwidth cannot be null");
//		}
		return new SwitchPortBandwidth(d, p, rx, tx);
	}
	
	public String getSwitchId() {
		return dpid;
	}
	
	public OFPort getSwitchPort() {
		return pt;
	}
	
	public long getBitsPerSecondRx() {
		return rx;
	}
	
	public long getBitsPerSecondTx() {
		return tx;
	}
	
	public long getUpdateTime() {
		return time.getTime();
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dpid == null) ? 0 : dpid.hashCode());
		result = prime * result + ((pt == null) ? 0 : pt.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwitchPortBandwidth other = (SwitchPortBandwidth) obj;
		if (dpid == null) {
			if (other.dpid != null)
				return false;
		} else if (!dpid.equals(other.dpid))
			return false;
		if (pt == null) {
			if (other.pt != null)
				return false;
		} else if (!pt.equals(other.pt))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "SwitchPortBandwidth [dpid=" + dpid + ", pt=" + pt + ", rx=" + rx + ", tx=" + tx + ", time=" + time
				+ "]";
	}
}
