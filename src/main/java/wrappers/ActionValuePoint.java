package wrappers;

import blacksmith.Actions;

public record ActionValuePoint(Actions action, int posX, int posY) {

    @Override
    public String toString() {
        return "ActionValuePoint{" + "action=" + action + ", posX=" + posX + ", posY=" + posY + '}';
    }
}
