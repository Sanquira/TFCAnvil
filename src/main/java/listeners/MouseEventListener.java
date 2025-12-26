package listeners;

import java.util.EventListener;
import listeners.events.MouseEvent;

public interface MouseEventListener extends EventListener {

    void mouseClicked(MouseEvent event);
}
