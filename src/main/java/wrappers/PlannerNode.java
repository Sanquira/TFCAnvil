package wrappers;

public record PlannerNode(ActionValuePoint actionValuePoint, int gValue, int fValue) {
    @Override
    public String toString() {
        return "Node["
                + "actionValuePoint="
                + actionValuePoint
                + ", "
                + "gValue="
                + gValue
                + ", "
                + "fValue="
                + fValue
                + ']';
    }
}
