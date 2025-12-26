package blacksmith;

import java.util.HashSet;
import listeners.ProgramStateListener;

public class StateMachine {

    private static final StateMachine instance;

    static {
        instance = new StateMachine();
    }

    HashSet<ProgramStateListener> programStateListeners;
    ProgramState currentState = ProgramState.INIT;

    private StateMachine() {
        programStateListeners = new HashSet<>();
    }

    public static StateMachine getInstance() {
        return instance;
    }

    public void addProgramStateListener(ProgramStateListener listener) {
        programStateListeners.add(listener);
    }

    public void removeProgramStateListeners(ProgramStateListener listener) {
        programStateListeners.remove(listener);
    }

    public ProgramState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ProgramState newState) {
        this.currentState = newState;
        for (ProgramStateListener listener : programStateListeners) {
            listener.programStateChanged(this.currentState);
        }
    }
}
