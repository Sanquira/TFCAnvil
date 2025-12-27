package listeners;

import blacksmith.ProgramState;

import java.util.EventListener;

public interface ProgramStateListener extends EventListener {

    void programStateChanged(ProgramState newState);

}
