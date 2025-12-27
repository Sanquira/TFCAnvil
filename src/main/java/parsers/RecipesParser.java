package parsers;

import blacksmith.Actions;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import wrappers.Recipe;

public class RecipesParser extends AbstractParser<List<Recipe>> {
    public RecipesParser(String filePath) {
        super(filePath);
        this.g = new GsonBuilder()
                .registerTypeAdapter(Recipe.class, new RecipeDeserializer())
                .create();
    }

    @Override
    protected Type getObjectType() {
        return new TypeToken<List<Recipe>>() {}.getType();
    }

    static class RecipeDeserializer implements JsonDeserializer<Recipe> {
        @Override
        public Recipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String name = obj.get("name").getAsString();
            JsonArray arr = obj.getAsJsonArray("finishingActions");
            List<Actions> actions = new ArrayList<>(arr.size());
            for (JsonElement element : arr) {
                String actName = element.getAsString();
                Actions value = null;
                for (Actions a : Actions.values()) {
                    if (a.name.equals(actName)) {
                        value = a;
                        break;
                    }
                }
                if (value == null) {
                    throw new JsonParseException("Unknown finishing action in recipe. (" + name + ")");
                }
                actions.add(value);
            }
            return new Recipe(name, actions);
        }
    }
}
