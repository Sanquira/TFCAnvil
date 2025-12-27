package gui;

import listeners.RecipeChangedEventListener;

public interface MainGUIInterface {
    void setGuideLabel(GuideLabel guideLabel);

    void setStatusLabel(StatusLabel label);

    void addRecipeChangedListener(RecipeChangedEventListener listener);

    void removeRecipeChangedListener(RecipeChangedEventListener listener);
}
