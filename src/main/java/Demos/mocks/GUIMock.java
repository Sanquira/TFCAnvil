package Demos.mocks;

import gui.GuideLabel;
import gui.MainGUIInterface;
import gui.StatusLabel;
import listeners.RecipeChangedEventListener;

public class GUIMock implements MainGUIInterface {
    @Override
    public void setGuideLabel(GuideLabel guideLabel) {
        System.out.println(guideLabel.getLabel());
    }

    @Override
    public void setStatusLabel(StatusLabel label) {
        System.out.println(label.getLabel());
    }

    @Override
    public void addRecipeChangedListener(RecipeChangedEventListener listener) {}
}
