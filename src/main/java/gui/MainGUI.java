package gui;

import blacksmith.ProgramState;
import blacksmith.StateMachine;
import listeners.RecipeChangedEventListener;
import wrappers.Recipe;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;

public class MainGUI extends JFrame implements MainGUIInterface {

    JLabel statusLabel = new JLabel();
    JLabel guideLabel = new JLabel(GuideLabel.createRecordGuide().getLabel());

    HashSet<RecipeChangedEventListener> recipeSelectionEventListener;

    public MainGUI(Map<String, Recipe> recipeMap) {
        recipeSelectionEventListener = new HashSet<>();

        setTitle("TF - blacksmith");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        JComboBox<String> recipeListComboBox = new JComboBox<>();
        recipeListComboBox.setEnabled(StateMachine.getInstance().getCurrentState().isRecipesEnabled());
        for (String recipeName : recipeMap.keySet()) {
            recipeListComboBox.addItem(recipeName);
        }
        recipeListComboBox.addActionListener(e -> {
            JComboBox<?> cb = (JComboBox<?>) e.getSource();
            String recipeName = (String) cb.getSelectedItem();
            Recipe newRecipe = recipeMap.get(recipeName);
            for (RecipeChangedEventListener listener : recipeSelectionEventListener) {
                listener.recipeChanged(newRecipe);
            }
        });
        add(recipeListComboBox, "North");
        add(statusLabel, "Center");
        add(guideLabel, "South");
        setSize(200, 100);
        setVisible(true);

        StateMachine.getInstance().addProgramStateListener(newState -> {
            if (newState == ProgramState.STARTED) {
                String recipeName = (String) recipeListComboBox.getSelectedItem();
                Recipe newRecipe = recipeMap.get(recipeName);
                for (RecipeChangedEventListener listener : recipeSelectionEventListener) {
                    listener.recipeChanged(newRecipe);
                }
            }
            recipeListComboBox.setEnabled(newState.isRecipesEnabled());
        });
    }

    @Override
    public void addRecipeChangedListener(RecipeChangedEventListener listener) {
        this.recipeSelectionEventListener.add(listener);
    }

    @Override
    public void removeRecipeChangedListener(RecipeChangedEventListener listener) {
        this.recipeSelectionEventListener.remove(listener);
    }

    @Override
    public void setGuideLabel(GuideLabel guideLabel) {
        this.guideLabel.setText(guideLabel.getLabel());
    }

    @Override
    public void setStatusLabel(StatusLabel label) {
        this.statusLabel.setText(label.getLabel());
    }
}
