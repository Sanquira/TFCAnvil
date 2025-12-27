package listeners;

import listeners.events.MouseEvent;

import java.util.EventListener;

public interface MouseEventListener extends EventListener {

    int getButton();

    void mouseClicked(MouseEvent event);

}
