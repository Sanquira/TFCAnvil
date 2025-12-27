package Demos;

import Demos.mocks.GUIMock;
import blacksmith.NativeListener;
import blacksmith.Recorder;
import parsers.ActionParser;
import wrappers.ActionValue;

import java.util.Map;

public class RecorderTest {
    public static void main(String[] args) {
        ActionParser parser = new ActionParser("action_config.json");
        Map<String, ActionValue> actionValueMap = parser.parseToMap();
        NativeListener nativeListener = new NativeListener();
        GUIMock guiMock = new GUIMock();
        Recorder recorder = new Recorder(guiMock, nativeListener, actionValueMap);
        recorder.enableListeners();
    }
}
