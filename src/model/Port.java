package model;

public class Port {
	int port_no;
	String hw_addr;
	String name;
	int config;
	int state;
	int curr;
	int advertised;
	int supported;
	int peer;
	int curr_speed;
	int max_speed;
	long rx_packets;
	long tx_packets;
	long rx_bytes;
	long tx_bytes;
	long rx_dropped;
	long tx_dropped;
	long rx_errors;
	long tx_errors;
	long rx_frame_err;
	long rx_over_err;
	long rx_crc_err;
	long collisions;
	int duration_sec;
	int duration_nsec;
	
	public Port(){
		port_no=-1;
		hw_addr=null;
		name=null;
		config=-1;
		state=-1;
		curr=-1;
		advertised=-1;
		supported=-1;
		peer=-1;
		curr_speed=-1;
		max_speed=-1;
		rx_packets=-1;
		tx_packets=-1;
		rx_bytes=-1;
		tx_bytes=-1;
		rx_dropped=-1;
		tx_dropped=-1;
		rx_errors=-1;
		tx_errors=-1;
		rx_frame_err=-1;
		rx_over_err=-1;
		rx_crc_err=-1;
		collisions=-1;
		duration_sec=-1;
		duration_nsec=-1;
	}
	
	public void setPortNo(int port_no){
		this.port_no = port_no;
	}
	public void setHWAddr(String hw_addr){
		this.hw_addr = hw_addr;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setConfig(int config){
		this.config = config;
	}
	public void setState(int state){
		this.state = state;
	}
	public void setCurr(int curr){
		this.curr = curr;
	}
	public void setAdvertised(int advertised){
		this.advertised = advertised;
	}
	public void setSupported(int supported){
		this.supported = supported;
	}
	public void setPeer(int peer){
		this.peer = peer;
	}
	public void setCurrSpeed(int curr_speed){
		this.curr_speed = curr_speed;
	}
	public void setMaxSpeed(int max_speed){
		this.max_speed = max_speed;
	}
	public void setRXPackets(long rx_packets){
		this.rx_packets = rx_packets;
	}
	public void setTXPackets(long tx_packets){
		this.tx_packets = tx_packets;
	}
	public void setRXBytes(long rx_bytes){
		this.rx_bytes = rx_bytes;
	}
	public void setTXBytes(long tx_bytes){
		this.tx_bytes = tx_bytes;
	}
	public void setRXDropped(long rx_dropped){
		this.rx_dropped = rx_dropped;
	}
	public void setTXDropped(long tx_dropped){
		this.tx_dropped = tx_dropped;
	}
	public void setRXErrors(long rx_errors){
		this.rx_errors = rx_errors;
	}
	public void setTXErrors(long tx_errors){
		this.tx_errors = tx_errors;
	}
	public void setRXFrameErr(long rx_frame_err){
		this.rx_frame_err = rx_frame_err;
	}
	public void setRXOverErr(long rx_over_err){
		this.rx_over_err = rx_over_err;
	}
	public void setRXCRCErr(long rx_crc_err){
		this.rx_crc_err = rx_crc_err;
	}
	public void setCollisions(long collisions) {
		this.collisions = collisions;
	}
	public void setDurationSec(int duration_sec){
		this.duration_sec = duration_sec;
	}
	public void setDurationNsec(int duration_nsec){
		this.duration_nsec = duration_nsec;
	}
	
	
	public int getPortNo(){
		return this.port_no;
	}
	public String getHWAddr(){
		return this.hw_addr;
	}
	public String getName(){
		return this.name;
	}
	public int getConfig(){
		return this.config;
	}
	public int getState(){
		return this.state;
	}
	/////////////////////////////////////////////////
	public String getPortState(){
		String state = null;
		switch(this.state & 1){
		case 0: 
			state = "UP";
			break;
		case 1:
			state = "DOWN";
			break;
		}
		return state;
	}
	public int getCurr(){
		return this.curr;
	}
	//////////////////////////////////////////////////////////
	public String getCurrentFeatures(){
		String currentFeatures = null;
		switch (this.curr & 127) {
		case 1:
            currentFeatures = " - 10 Mbps";
            break;
        case 2:
        	currentFeatures = " - 10 Mbps FDX";
            break;
        case 4:
        	currentFeatures = " - 100 Mbps";
            break;
        case 8:
        	currentFeatures = " - 100 Mbps FDX";
            break;
        case 16:
        	currentFeatures = " - 1 Gbps"; // RLY?
            break;
        case 32:
        	currentFeatures = " - 1 Gbps FDX";
            break;
        case 64:
        	currentFeatures = " - 10 Gbps FDX";
            break;
        default:
        	currentFeatures = "";
        	break;
		}
		return currentFeatures;
	}
	public int getAdvertised(){
		return this.advertised;
	}
	public int getSupported(){
		return this.supported;
	}
	public int getPeer(){
		return this.peer;
	}
	public int getCurrSpeed(){
		return this.curr_speed;
	}
	public int getMaxSpeed(){
		return this.max_speed;
	}
	public long getRXPackets(){
		return this.rx_packets;
	}
	public long getTXPackets(){
		return this.tx_packets;
	}
	public long getRXBytes(){
		return this.rx_bytes;
	}
	public long getTXBytes(){
		return this.tx_bytes;
	}
	public long getRXDropped(){
		return this.rx_dropped;
	}
	public long getTXDropped(){
		return this.tx_dropped;
	}
	public long getRXErrors(){
		return this.rx_errors;
	}
	public long getTXErrors(){
		return this.tx_errors;
	}
	public long getRXFrameErr(){
		return this.rx_frame_err;
	}
	public long getRXOverErr(){
		return this.rx_over_err;
	}
	public long getRXCRCErr(){
		return this.rx_crc_err;
	}
	public long getCollisions() {
		return this.collisions;
	}
	public int getDurationSec(){
		return this.duration_sec;
	}
	public int getDurationNsec(){
		return this.duration_nsec;
	}
}
