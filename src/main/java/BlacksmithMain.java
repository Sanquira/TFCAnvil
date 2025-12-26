import blacksmith.*;
import gui.MainGUI;
import java.util.Map;
import parsers.RecipesParser;
import wrappers.Recipe;

public class BlacksmithMain {
    public static void main(String[] args) {
        RecipesParser recipesParser = new RecipesParser("recipes.json");
        Map<String, Recipe> recipeMap = recipesParser.parseToMap();

        NativeListener nativeListener = new NativeListener();

        MainGUI gui = new MainGUI(recipeMap);

        Recorder recorder = new Recorder(gui, nativeListener);
        recorder.enableListeners();

        Blacksmith blacksmith = new Blacksmith(gui, nativeListener);
        StateMachine.getInstance().addProgramStateListener(newState -> {
            if (newState == ProgramState.RECORDED) {
                blacksmith.UpdateScanner(recorder.getRectangle());
                blacksmith.UpdateToolPositions(recorder.getActionValuePointMap());
            }
        });

        StateMachine.getInstance().setCurrentState(ProgramState.STARTED);
    }
}
