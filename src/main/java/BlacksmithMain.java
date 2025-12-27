import blacksmith.*;
import gui.MainGUI;
import parsers.ActionParser;
import parsers.RecipesParser;
import wrappers.ActionValue;
import wrappers.Recipe;

import java.util.Map;

public class BlacksmithMain {
    public static void main(String[] args) {
        ActionParser actionParser = new ActionParser("action_config.json");
        Map<String, ActionValue> actionValueMap = actionParser.parseToMap();

        RecipesParser recipesParser = new RecipesParser("recipes.json", actionValueMap);
        Map<String, Recipe> recipeMap = recipesParser.parseToMap();

        NativeListener nativeListener = new NativeListener();

        MainGUI gui = new MainGUI(recipeMap);

        Recorder recorder = new Recorder(gui, nativeListener, actionValueMap);
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
