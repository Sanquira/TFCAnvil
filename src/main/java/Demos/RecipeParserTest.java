package Demos;

import java.util.Map;
import parsers.ActionParser;
import parsers.RecipesParser;
import wrappers.ActionValue;
import wrappers.Recipe;

public class RecipeParserTest {
    public static void main(String[] args) {
        Map<String, ActionValue> actionValueMap = new ActionParser("action_config.json").parseToMap();

        RecipesParser parser = new RecipesParser("recipes.json", actionValueMap);
        Map<String, Recipe> stringRecipeMap = parser.parseToMap();
        for (Recipe recipe : stringRecipeMap.values()) {
            System.out.println(recipe);
        }
    }
}
