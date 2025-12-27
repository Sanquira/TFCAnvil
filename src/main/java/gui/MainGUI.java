package gui;

import blacksmith.ProgramState;
import blacksmith.StateMachine;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import listeners.RecipeChangedEventListener;
import wrappers.Recipe;

public class MainGUI extends JFrame implements MainGUIInterface {

    JLabel statusLabel = new JLabel();
    JLabel guideLabel = new JLabel(GuideLabel.createRecordGuide().getLabel());
    JComboBox<String> recipeListComboBox;
    JTextField searchField;
    Map<String, Recipe> recipeMap;

    HashSet<RecipeChangedEventListener> recipeSelectionEventListener;
    JButton reloadButton;
    String recipeFileName;

    public MainGUI(String recipeFileName) {
        this.recipeFileName = recipeFileName;
        this.recipeMap = loadRecipes();
        recipeSelectionEventListener = new HashSet<>();

        setTitle("TFC - Blacksmith");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());

        // Create search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setToolTipText("Search recipes...");
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Add reload button
        reloadButton = new JButton("Reload");
        reloadButton.setToolTipText("Reload recipes.json");
        reloadButton.addActionListener(e -> reloadRecipes());
        searchPanel.add(reloadButton, BorderLayout.EAST);

        // Create recipe combo box
        recipeListComboBox = new JComboBox<>();
        populateRecipeComboBox("");

        // Use GridBagLayout for full-width dropdowns, but only recipeListComboBox now
        JPanel recipePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        recipePanel.add(recipeListComboBox, gbc);
        recipePanel.setPreferredSize(
                new Dimension(searchPanel.getPreferredSize().width, recipeListComboBox.getPreferredSize().height));

        // Add search filter listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterRecipes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterRecipes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterRecipes();
            }
        });

        recipeListComboBox.addActionListener(e -> {
            String recipeName = (String) recipeListComboBox.getSelectedItem();
            if (recipeName != null) {
                Recipe newRecipe = recipeMap.get(recipeName);
                notifyRecipeChanged(newRecipe);
            }
        });

        // Layout components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(recipePanel, BorderLayout.SOUTH);

        // Add status and guide labels
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(guideLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.CENTER);
        setSize(250, 180);
        setVisible(true);

        StateMachine.getInstance().addProgramStateListener(newState -> {
            if (newState == ProgramState.STARTED) {
                String recipeName = (String) recipeListComboBox.getSelectedItem();
                if (recipeName != null) {
                    Recipe newRecipe = recipeMap.get(recipeName);
                    notifyRecipeChanged(newRecipe);
                }
            }
            recipeListComboBox.setEnabled(newState.isRecipesEnabled());
            searchField.setEnabled(newState.isRecipesEnabled());
        });
    }

    private void populateRecipeComboBox(String filter) {
        String currentSelection = (String) recipeListComboBox.getSelectedItem();
        recipeListComboBox.removeAllItems();
        recipeListComboBox.setEnabled(
                StateMachine.getInstance().getCurrentState().isRecipesEnabled());

        String lowerFilter = filter.toLowerCase();
        boolean hasItems = false;

        for (String recipeName : recipeMap.keySet()) {
            if (filter.isEmpty() || recipeName.toLowerCase().contains(lowerFilter)) {
                recipeListComboBox.addItem(recipeName);
                hasItems = true;
            }
        }

        // Restore selection if it still matches the filter
        if (currentSelection != null && currentSelection.toLowerCase().contains(lowerFilter)) {
            recipeListComboBox.setSelectedItem(currentSelection);
        } else if (hasItems) {
            recipeListComboBox.setSelectedIndex(0);
        }
    }

    private void filterRecipes() {
        String searchText = searchField.getText();
        populateRecipeComboBox(searchText);
    }

    private void notifyRecipeChanged(Recipe newRecipe) {
        for (RecipeChangedEventListener listener : recipeSelectionEventListener) {
            listener.recipeChanged(newRecipe);
        }
    }

    @Override
    public void addRecipeChangedListener(RecipeChangedEventListener listener) {
        this.recipeSelectionEventListener.add(listener);
    }

    @Override
    public void setGuideLabel(GuideLabel guideLabel) {
        this.guideLabel.setText(guideLabel.getLabel());
    }

    @Override
    public void setStatusLabel(StatusLabel label) {
        this.statusLabel.setText(label.getLabel());
    }

    private Map<String, Recipe> loadRecipes() {
        try {
            parsers.RecipesParser parser = new parsers.RecipesParser(recipeFileName);
            return new TreeMap<>(parser.parse().stream().collect(Collectors.toMap(Recipe::name, recipe -> recipe)));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this, "Error loading recipes.json: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return new java.util.HashMap<>();
    }

    private void reloadRecipes() {
        this.recipeMap = loadRecipes();
        filterRecipes();
    }
}
