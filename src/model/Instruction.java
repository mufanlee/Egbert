package model;

import java.util.List;

public interface Instruction {

	OFInstructionType getType();

	List<IAction> getActions();

	void setActions(List<IAction> actions);

}
