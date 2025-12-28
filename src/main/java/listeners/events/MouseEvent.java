package listeners.events;

import blacksmith.CoordinateTransformer;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import java.util.EventObject;

public class MouseEvent extends EventObject {
    private int x;

    private int y;

    private int button;

    /**
     * Constructs a prototypical Event.
     * Transforms physical coordinates from NativeMouseEvent to logical coordinates.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public MouseEvent(NativeMouseEvent source) {
        super(source.getSource());
        // Transform physical coordinates (from native event) to logical coordinates (for Robot)
        this.x = CoordinateTransformer.physicalToLogicalX(source.getX());
        this.y = CoordinateTransformer.physicalToLogicalY(source.getY());
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
