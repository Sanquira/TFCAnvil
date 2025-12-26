package parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import wrappers.ActionValue;
import wrappers.Recipe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipesParser extends AbstractParser<List<Recipe>> {
    Map<String, ActionValue> actionValuesMap;

    public RecipesParser(String filePath, Map<String, ActionValue> actionValues) {
        super(filePath);
        this.actionValuesMap = actionValues;
        this.g = new GsonBuilder()
                .registerTypeAdapter(Recipe.class, new RecipeDeserializer(actionValues))
                .create();
    }

    public Map<String, Recipe> parseToMap() {
        List<Recipe> recipesList = super.parse();
        if (recipesList == null) return null;
        Map<String, Recipe> recipes = new HashMap<>();
        for (Recipe recipe : recipesList) {
            recipes.put(recipe.name(), recipe);
        }
        return recipes;
    }

    @Override
    protected Type getObjectType() {
        return new TypeToken<List<Recipe>>() {
        }.getType();
    }

    static class RecipeDeserializer implements JsonDeserializer<Recipe> {
        private final Map<String, ActionValue> actionMap;

        RecipeDeserializer(Map<String, ActionValue> actionMap) {
            this.actionMap = actionMap;
        }

        @Override
        public Recipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String name = obj.get("name").getAsString();
            JsonArray arr = obj.getAsJsonArray("finishingActions");
            List<ActionValue> actions = new ArrayList<>(arr.size());
            for (JsonElement element : arr) {
                String actName = element.getAsString();
                ActionValue value = actionMap.get(actName);
                if (value == null) {
                    throw new JsonParseException("Unknown finishing action in recipe. (" + name + ")");
                }
                actions.add(value);
            }
            return new Recipe(name, actions);
        }
    }
}
