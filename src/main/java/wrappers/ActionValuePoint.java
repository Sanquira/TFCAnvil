package wrappers;

public record ActionValuePoint(ActionValue actionValue, int posX, int posY) {

    @Override
    public String toString() {
        return "ActionValuePoint{" + "actionValue=" + actionValue + ", posX=" + posX + ", posY=" + posY + '}';
    }
}
