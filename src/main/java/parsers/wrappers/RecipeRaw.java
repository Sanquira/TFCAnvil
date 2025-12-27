package parsers.wrappers;

import java.util.Arrays;

public class RecipeRaw {
    String name;
    String[] finishingActions;

    public RecipeRaw(String name, String[] finishingActions) {
        this.name = name;
        this.finishingActions = finishingActions;
    }

    public String getName() {
        return name;
    }

    public String[] getFinishingActions() {
        return finishingActions;
    }

    @Override
    public String toString() {
        return "RecipeRaw{" +
                "name='" + name + '\'' +
                ", finishingActions=" + Arrays.toString(finishingActions) +
                '}';
    }
}
