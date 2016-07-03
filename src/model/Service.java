package model;

public class Service implements IPolicyType{

	protected long id;
	protected String name;
	protected byte tos;
	protected String type;
	protected String qoSTypeID;
	protected int port;
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Service(){
		this.id = -1;
		this.name = null;
		this.tos = 0x00;
		this.port = -1;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getTos() {
		return tos;
	}

	public void setTos(byte tos) {
		this.tos = tos;
	}
	
	@Override
	public String toString() {
		return "Service [id=" + id + ", name=" + name + ", tos=" + tos + ", port=" + port +"]";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "Service";
	}

	@Override
	public String getQoSTypeID() {
		// TODO Auto-generated method stub
		return getType() + getId();
	}
}
