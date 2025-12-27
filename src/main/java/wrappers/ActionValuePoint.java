package wrappers;

import java.util.Objects;

public final class ActionValuePoint {
    private final ActionValue actionValue;
    private final int posX;
    private final int posY;

    public ActionValuePoint(ActionValue actionValue, int posX, int posY) {
        this.actionValue = actionValue;
        this.posX = posX;
        this.posY = posY;
    }

    public ActionValue actionValue() {
        return actionValue;
    }

    public int posX() {
        return posX;
    }

    public int posY() {
        return posY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ActionValuePoint that = (ActionValuePoint) obj;
        return Objects.equals(this.actionValue, that.actionValue) &&
                this.posX == that.posX &&
                this.posY == that.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionValue, posX, posY);
    }

    @Override
    public String toString() {
        return "ActionValuePoint{" +
                "actionValue=" + actionValue +
                ", posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
