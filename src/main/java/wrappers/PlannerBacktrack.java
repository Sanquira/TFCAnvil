package wrappers;

import java.util.Objects;

public final class PlannerBacktrack {
    private final ActionValuePoint actionValuePoint;
    private final int cameFrom;

    public PlannerBacktrack(ActionValuePoint actionValuePoint, int cameFrom) {
        this.actionValuePoint = actionValuePoint;
        this.cameFrom = cameFrom;
    }

    public ActionValuePoint actionValuePoint() {
        return actionValuePoint;
    }

    public int cameFrom() {
        return cameFrom;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        PlannerBacktrack that = (PlannerBacktrack) obj;
        return Objects.equals(this.actionValuePoint, that.actionValuePoint) &&
                this.cameFrom == that.cameFrom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionValuePoint, cameFrom);
    }

    @Override
    public String toString() {
        return "PlannerBacktrack[" +
                "actionValuePoint=" + actionValuePoint + ", " +
                "cameFrom=" + cameFrom + ']';
    }

}
