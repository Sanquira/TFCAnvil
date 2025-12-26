package listeners;

import java.util.EventListener;
import listeners.events.MouseEvent;

public interface MouseEventListener extends EventListener {

    int getButton();

    void mouseClicked(MouseEvent event);
}
