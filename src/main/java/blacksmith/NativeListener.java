package blacksmith;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import listeners.KeyEventListener;
import listeners.MouseEventListener;
import listeners.events.MouseEvent;

public class NativeListener implements NativeKeyListener, NativeMouseInputListener {

    Map<Integer, HashSet<KeyEventListener>> nativeKeyPressed;
    Map<Integer, HashSet<MouseEventListener>> nativeMouseClicked;

    public NativeListener() {
        this.nativeKeyPressed = new HashMap<>();
        this.nativeMouseClicked = new HashMap<>();

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        GlobalScreen.addNativeMouseListener(this);
    }

    public void addKeyPressedListener(int keyCode, KeyEventListener listener) {
        if (!this.nativeKeyPressed.containsKey(keyCode)) {
            this.nativeKeyPressed.put(keyCode, new HashSet<>());
        }
        this.nativeKeyPressed.get(keyCode).add(listener);
    }

    public void removeKeyPressedListener(int keyCode, KeyEventListener listener) {
        if (!this.nativeKeyPressed.containsKey(keyCode)) {
            return;
        }
        this.nativeKeyPressed.get(keyCode).remove(listener);
        if (this.nativeKeyPressed.get(keyCode).isEmpty()) {
            this.nativeKeyPressed.remove(keyCode);
        }
    }

    public void addMouseClickedListener(int button, MouseEventListener listener) {
        if (!this.nativeMouseClicked.containsKey(button)) {
            this.nativeMouseClicked.put(button, new HashSet<>());
        }
        this.nativeMouseClicked.get(button).add(listener);
    }

    public void removeMouseClickedListener(int button, MouseEventListener listener) {
        if (!this.nativeMouseClicked.containsKey(button)) {
            return;
        }
        this.nativeMouseClicked.get(button).remove(listener);
        if (this.nativeMouseClicked.get(button).isEmpty()) {
            this.nativeMouseClicked.remove(button);
        }
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        int key = e.getKeyCode();
        if (this.nativeKeyPressed.containsKey(key)) {
            HashSet<KeyEventListener> eventListeners = this.nativeKeyPressed.get(key);
            for (KeyEventListener eventListener : eventListeners) {
                eventListener.keyPressed();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {}

    public void nativeKeyTyped(NativeKeyEvent e) {}

    public void nativeMouseDragged(NativeMouseEvent e) {}

    public void nativeMouseMoved(NativeMouseEvent e) {}

    public void nativeMouseClicked(NativeMouseEvent e) {
        int key = e.getButton();
        if (this.nativeMouseClicked.containsKey(key)) {
            HashSet<MouseEventListener> eventListeners = this.nativeMouseClicked.get(key);
            for (MouseEventListener eventListener : eventListeners) {
                eventListener.mouseClicked(new MouseEvent(e));
            }
        }
    }

    public void nativeMousePressed(NativeMouseEvent e) {}

    public void nativeMouseReleased(NativeMouseEvent e) {}
}
