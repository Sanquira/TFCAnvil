package parsers;

import com.google.gson.reflect.TypeToken;
import parsers.wrappers.RecipeRaw;
import wrappers.ActionValue;
import wrappers.Recipe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipesParser extends AbstractParser<List<RecipeRaw>> {
    Map<String, ActionValue> actionValuesMap;

    public RecipesParser(String filePath, Map<String, ActionValue> actionValues) {
        super(filePath);
        this.actionValuesMap = actionValues;
    }

    public Map<String, Recipe> parseToMap() {
        List<RecipeRaw> recipesRaw = super.parse();
        Map<String, Recipe> recipes = new HashMap<>();
        for (RecipeRaw recipeRaw : recipesRaw) {
            String[] finishingActions = recipeRaw.finishingActions();
            List<ActionValue> actionValueList = new ArrayList<>(finishingActions.length);
            for (String actionValue : finishingActions) {
                ActionValue action = actionValuesMap.get(actionValue);
                if (action == null) {
                    System.err.println("Unknown finishing action in recipe. (" + recipeRaw.name() + ")");
                    return null;
                }
                actionValueList.add(actionValuesMap.get(actionValue));
            }
            recipes.put(recipeRaw.name(), new Recipe(recipeRaw.name(), actionValueList));
        }
        return recipes;
    }

    @Override
    protected Type getObjectType() {
        return new TypeToken<List<RecipeRaw>>() {
        }.getType();
    }
}
