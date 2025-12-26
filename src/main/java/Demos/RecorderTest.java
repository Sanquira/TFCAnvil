package Demos;

import Demos.mocks.GUIMock;
import blacksmith.NativeListener;
import blacksmith.Recorder;

public class RecorderTest {
    public static void main(String[] args) {
        NativeListener nativeListener = new NativeListener();
        GUIMock guiMock = new GUIMock();
        Recorder recorder = new Recorder(guiMock, nativeListener);
        recorder.enableListeners();
        System.out.println("Recorder enabled. Actions are now defined in the Actions enum.");
    }
}
