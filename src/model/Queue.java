package model;

public class Queue implements IPolicyType{

	protected long id;
	protected String name;
	protected short port;
	protected short queue;
	protected String type;
	protected String qoSTypeID;
	
	public Queue(){
		id = -1;
		name = null;
		port = -1;
		queue = -1;
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
	public short getPort() {
		return port;
	}
	public void setPort(short port) {
		this.port = port;
	}
	public short getQueue() {
		return queue;
	}
	public void setQueue(short queue) {
		this.queue = queue;
	}
	@Override
	public String toString() {
		return "Queue [id=" + id + ", name=" + name + ", port=" + port
				+ ", queue=" + queue + "]";
	}
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "Enqueue";
	}
	@Override
	public String getQoSTypeID() {
		// TODO Auto-generated method stub
		return getType() + getId();
	}
}
