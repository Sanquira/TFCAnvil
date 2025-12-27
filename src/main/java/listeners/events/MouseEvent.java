package listeners.events;


import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.util.EventObject;

public class MouseEvent extends EventObject {
    private int x;

    private int y;

    private int button;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public MouseEvent(NativeMouseEvent source) {
        super(source.getSource());
        this.x = source.getX();
        this.y = source.getY();
        this.button = source.getButton();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getButton() {
        return button;
    }
}
