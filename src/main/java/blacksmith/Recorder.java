package blacksmith;

import gui.GuideLabel;
import gui.MainGUIInterface;
import gui.StatusLabel;
import listeners.KeyEventListener;
import listeners.MouseEventListener;
import listeners.ToggableListeners;
import listeners.events.MouseEvent;
import wrappers.ActionValue;
import wrappers.ActionValuePoint;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Recorder implements ToggableListeners {

    private final MainGUIInterface gui;
    private final NativeListener nativeListener;
    private final Map<String, ActionValue> actionValueMap;
    private final Map<String, ActionValuePoint> actionValuePointMap;
    private boolean isRecording = false;
    private KeyEventListener keyListener = null;
    private MouseEventListener mouseListener = null;
    private Iterator<Map.Entry<String, ActionValue>> actionIterator;
    private ActionValue currentActionValue;

    private Point leftTop, rightBottom;

    public Recorder(MainGUIInterface gui, NativeListener nativeListener, Map<String, ActionValue> actionValueMap) {
        this.gui = gui;
        this.nativeListener = nativeListener;
        this.actionValueMap = actionValueMap;
        this.actionValuePointMap = new HashMap<>();
        StopRecording();

        StateMachine.getInstance().addProgramStateListener(newState -> {
            if (newState.isRecordingEnabled()) {
                enableListeners();
            } else {
                disableListeners();
            }
        });
    }

    @Override
    public void enableListeners() {
        if (keyListener == null) {
            keyListener = new KeyEventListener() {
                @Override
                public int getKey() {
                    return 65; //F7
                }

                @Override
                public void keyPressed() {
                    if (isRecording()) {
                        StopRecording();
                    } else {
                        StartRecording();
                    }
                }
            };
        }
        if (mouseListener == null) {
            mouseListener = new MouseEventListener() {
                @Override
                public int getButton() {
                    return 1;
                }

                @Override
                public void mouseClicked(MouseEvent event) {
                    if (!isRecording()) {
                        return;
                    }

                    if (currentActionValue != null) {
                        actionValuePointMap.put(currentActionValue.getName(),
                                new ActionValuePoint(currentActionValue, event.getX(), event.getY()));
                        SelectNextAction();
                        return;
                    }
                    if (leftTop == null) {
                        leftTop = new Point(event.getX(), event.getY());
                        SelectNextAction();
                        return;
                    }
                    if (rightBottom == null) {
                        rightBottom = new Point(event.getX(), event.getY());
                        SelectNextAction();
                        return;
                    }
                    System.err.println("Still recording despite all data is filled.");
                    SelectNextAction();
                }
            };
        }
        nativeListener.addKeyPressedListener(keyListener);
        nativeListener.addMouseClickedListener(mouseListener);
    }

    @Override
    public void disableListeners() {
        nativeListener.removeKeyPressedListener(keyListener);
        nativeListener.removeMouseClickedListener(mouseListener);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void StartRecording() {
        StateMachine.getInstance().setCurrentState(ProgramState.RECORDING);
        this.isRecording = true;
        gui.setStatusLabel(StatusLabel.RECORDING);
        ClearData();
        SelectNextAction();
    }

    public void StopRecording() {
        this.isRecording = false;
        gui.setStatusLabel(StatusLabel.NOT_RECORDING);
        gui.setGuideLabel(GuideLabel.createRecordGuide());
        if (currentActionValue == null && leftTop != null && rightBottom != null) {
            StateMachine.getInstance().setCurrentState(ProgramState.RECORDED);
            gui.setStatusLabel(StatusLabel.RECORDED);
            gui.setGuideLabel(GuideLabel.createBlacksmithReadyLabel());
            return;
        }
        ClearData();
        StateMachine.getInstance().setCurrentState(ProgramState.STARTED);
    }

    private void SelectNextAction() {
        if (actionIterator == null) {
            actionIterator = this.actionValueMap.entrySet().iterator();
        }
        if (actionIterator.hasNext()) {
            Map.Entry<String, ActionValue> actionValueEntry = actionIterator.next();
            currentActionValue = actionValueEntry.getValue();
            gui.setGuideLabel(GuideLabel.createActionRecordingLabel(actionValueEntry.getKey()));
            return;
        }
        currentActionValue = null;
        if (leftTop == null) {
            gui.setGuideLabel(GuideLabel.createLeftTopPositionRecordingLabel());
            return;
        }
        if (rightBottom == null) {
            gui.setGuideLabel(GuideLabel.createRightBottomPositionRecordingLabel());
            return;
        }
        StopRecording();
    }

    private void ClearData() {
        actionIterator = null;
        actionValuePointMap.clear();
        leftTop = null;
        rightBottom = null;
        currentActionValue = null;
    }

    public Map<String, ActionValuePoint> getActionValuePointMap() {
        return actionValuePointMap;
    }

    public Rectangle getRectangle() {
        int sizeX = Math.abs(leftTop.x - rightBottom.x);
        int sizeY = Math.abs(leftTop.y - rightBottom.y);
        return new Rectangle(leftTop, new Dimension(sizeX, sizeY));
    }
}
