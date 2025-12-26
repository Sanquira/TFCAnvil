package listeners;

import java.util.EventListener;

public interface KeyEventListener extends EventListener {

    int getKey();

    void keyPressed();
}
