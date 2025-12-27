import blacksmith.*;
import gui.MainGUI;

public class BlacksmithMain {
    public static void main(String[] args) {
        NativeListener nativeListener = new NativeListener();
        MainGUI gui = new MainGUI("recipes.json");

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
