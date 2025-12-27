package wrappers;


import java.util.Objects;

public final class PlannerNode {
    private final ActionValuePoint actionValuePoint;
    private final int gValue;
    private final int fValue;

    public PlannerNode(ActionValuePoint actionValuePoint, int gValue, int fValue) {
        this.actionValuePoint = actionValuePoint;
        this.gValue = gValue;
        this.fValue = fValue;
    }

    public ActionValuePoint actionValuePoint() {
        return actionValuePoint;
    }

    public int gValue() {
        return gValue;
    }

    public int fValue() {
        return fValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        PlannerNode that = (PlannerNode) obj;
        return Objects.equals(this.actionValuePoint, that.actionValuePoint) &&
                this.gValue == that.gValue &&
                this.fValue == that.fValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionValuePoint, gValue, fValue);
    }

    @Override
    public String toString() {
        return "Node[" +
                "actionValuePoint=" + actionValuePoint + ", " +
                "gValue=" + gValue + ", " +
                "fValue=" + fValue + ']';
    }

}

