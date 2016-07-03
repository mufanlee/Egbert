package model;

public class ActionOutput implements IAction {
	
    private OFPort port;
    private int maxLen;
    @Override
    public OFActionType getType() {
        return OFActionType.OUTPUT;
    }
	public OFPort getPort() {
		return port;
	}
	public void setPort(OFPort port) {
		this.port = port;
	}
	public int getMaxLen() {
		return maxLen;
	}
	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("OFActionOutput(");
        b.append("port=").append(port);
        b.append(", ");
        b.append("maxLen=").append(maxLen);
        b.append(")");
        return b.toString();
    }
}
