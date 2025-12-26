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

    public void addKeyPressedListener(KeyEventListener listener) {
        int key = listener.getKey();
        if (!this.nativeKeyPressed.containsKey(key)) {
            this.nativeKeyPressed.put(key, new HashSet<>());
        }
        this.nativeKeyPressed.get(key).add(listener);
    }

    public void removeKeyPressedListener(KeyEventListener listener) {
        int key = listener.getKey();
        if (!this.nativeKeyPressed.containsKey(key)) {
            return;
        }
        this.nativeKeyPressed.get(key).remove(listener);
        if (this.nativeKeyPressed.get(key).size() == 0) {
            this.nativeKeyPressed.remove(key);
        }
    }

    public void addMouseClickedListener(MouseEventListener listener) {
        int key = listener.getButton();
        if (!this.nativeMouseClicked.containsKey(key)) {
            this.nativeMouseClicked.put(key, new HashSet<>());
        }
        this.nativeMouseClicked.get(key).add(listener);
    }

    public void removeMouseClickedListener(MouseEventListener listener) {
        int key = listener.getButton();
        if (!this.nativeMouseClicked.containsKey(key)) {
            return;
        }
        this.nativeMouseClicked.get(key).remove(listener);
        if (this.nativeMouseClicked.get(key).size() == 0) {
            this.nativeMouseClicked.remove(key);
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
