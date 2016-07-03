package model;

public interface IAction {

	OFActionType getType();
	OFPort getPort();
	void setPort(OFPort port);

}
