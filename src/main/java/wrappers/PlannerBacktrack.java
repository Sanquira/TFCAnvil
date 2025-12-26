package wrappers;

public record PlannerBacktrack(ActionValuePoint actionValuePoint, int cameFrom) {
    public ActionValuePoint actionValuePoint() {
        return actionValuePoint;
    }

    public int cameFrom() {
        return cameFrom;
    }

    @Override
    public String toString() {
        return "PlannerBacktrack[" + "actionValuePoint=" + actionValuePoint + ", " + "cameFrom=" + cameFrom + ']';
    }
}
