package wrappers;

import java.util.List;

public record Recipe(String name, List<ActionValue> finishingActions, int startValue) {
    public Recipe(String name, List<ActionValue> finishingActions) {
        this(name, finishingActions, calculateStartValue(finishingActions));
    }

    private static int calculateStartValue(List<ActionValue> actions) {
        int tmp = 0;
        for (ActionValue action : actions) {
            tmp -= action.value();
        }
        return tmp;
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