package wrappers;

import blacksmith.Actions;
import java.util.List;

public record Recipe(String name, List<Actions> finishingActions, int startValue) {
    public Recipe(String name, List<Actions> finishingActions) {
        this(name, finishingActions, calculateStartValue(finishingActions));
    }

    private static int calculateStartValue(List<Actions> actions) {
        int tmp = 0;
        for (Actions action : actions) {
            tmp -= action.value;
        }
        return tmp;
    }

    @Override
    public String toString() {
        return "Recipe{" + "name='"
                + name + '\'' + ", finishingActions="
                + finishingActions + ", startValue="
                + startValue + '}';
    }
}
