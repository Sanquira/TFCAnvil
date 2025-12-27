package listeners;

import wrappers.Recipe;

import java.util.EventListener;

public interface RecipeChangedEventListener extends EventListener {
    void recipeChanged(Recipe newRecipe);
}
