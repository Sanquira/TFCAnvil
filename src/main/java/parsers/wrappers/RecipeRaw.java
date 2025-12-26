package parsers.wrappers;

import java.util.Arrays;

public record RecipeRaw (String name, String[] finishingActions) {
    @Override
    public String toString() {
        return "RecipeRaw{" +
                "name='" + name + '\'' +
                ", finishingActions=" + Arrays.toString(finishingActions) +
                '}';
    }
}
