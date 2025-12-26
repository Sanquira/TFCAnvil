package Demos;

import java.util.Map;
import parsers.RecipesParser;
import wrappers.Recipe;

public class RecipeParserTest {
    public static void main(String[] args) {
        RecipesParser parser = new RecipesParser("recipes.json");
        Map<String, Recipe> stringRecipeMap = parser.parseToMap();
        if (stringRecipeMap != null) {
            for (Recipe recipe : stringRecipeMap.values()) {
                System.out.println(recipe);
            }
        } else {
            System.out.println("Failed to parse recipes");
        }
    }
}
