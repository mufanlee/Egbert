package model;

import java.util.List;

public class InstructionApplyActions implements Instruction {
	private List<IAction> actions;
	
	@Override
    public OFInstructionType getType() {
        return OFInstructionType.APPLY_ACTIONS;
    }
	@Override
	public List<IAction> getActions() {
		return actions;
	}
	@Override
	public void setActions(List<IAction> actions) {
		this.actions = actions;
	}
	
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("OFInstructionApplyActions(");
        b.append("actions=").append(actions);
        b.append(")");
        return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InstructionApplyActions other = (InstructionApplyActions) obj;

        if (actions == null) {
            if (other.actions != null)
                return false;
        } else if (!actions.equals(other.actions))
            return false;
        return true;
    }
}
