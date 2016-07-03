package model;

public class Meter implements IPolicyType{

	protected long id;
	protected String name;
	protected String bandType;
	protected long rate;
	protected int burst;
	protected long maxrate;
	protected String type;
	protected String qoSTypeID;
	protected int port;
	
	
	public Meter() {
		id = -1;
		name = null;
		bandType = null;
		rate = -1;
		burst = -1;
		maxrate = -1;
		port = -1;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getId() {
		return id;
	}
	public void setId(long mid) {
		this.id = mid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBandType() {
		return bandType;
	}
	public void setBandType(String bandtype) {
		this.bandType = bandtype;
	}
	public long getRate() {
		return rate;
	}
	public void setRate(long rate) {
		this.rate = rate;
	}
	public int getBurst() {
		return burst;
	}
	public void setBurst(int burst) {
		this.burst = burst;
	}
	public long getMaxrate() {
		return maxrate;
	}
	public void setMaxrate(long maxrate) {
		this.maxrate = maxrate;
	}
	@Override
	public String toString() {
		return "Meter [id=" + id + ", name=" + name + ", bandtype="
				+ bandType + ", rate=" + rate + ", burst=" + burst
				+ ", maxrate=" + maxrate + ", port=" + port + "]";
	}
	@Override
	public String getType() {
		return "Addmeters";
	}
	@Override
	public String getQoSTypeID() {
		return getType() + getId();
	}
	
}
