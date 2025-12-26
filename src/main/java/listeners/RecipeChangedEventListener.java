package listeners;

import java.util.EventListener;
import wrappers.Recipe;

public interface RecipeChangedEventListener extends EventListener {
    void recipeChanged(Recipe newRecipe);
}
