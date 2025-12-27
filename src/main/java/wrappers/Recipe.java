package wrappers;

import java.util.List;

public class Recipe {
    private final String name;
    private final List<ActionValue> finishingActions;
    private final int startValue;

    public Recipe(String name, List<ActionValue> actions) {
        this.name = name;
        this.finishingActions = actions;
        int tmp = 0;
        for (ActionValue action : actions) {
            tmp -= action.getValue();
        }
        this.startValue = tmp;
    }

    public String getName() {
        return name;
    }

    public List<ActionValue> getFinishingActions() {
        return finishingActions;
    }

    public int getStartValue() {
        return startValue;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", finishingActions=" + finishingActions +
                ", startValue=" + startValue +
                '}';
    }
}