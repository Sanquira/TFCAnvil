package blacksmith;

import gui.GuideLabel;
import gui.MainGUIInterface;
import gui.StatusLabel;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import listeners.KeyEventListener;
import listeners.MouseEventListener;
import listeners.ToggableListeners;
import wrappers.ActionValuePoint;

record ActionGuiIndex(int idxX, int idxY) {}

public class Recorder implements ToggableListeners {

    private final MainGUIInterface gui;
    private final NativeListener nativeListener;
    private final Map<String, ActionValuePoint> actionValuePointMap;
    private boolean isRecording = false;
    private KeyEventListener keyListener = null;
    private MouseEventListener mouseListener = null;
    private Iterator<Actions> actionIterator;
    private Actions currentAction;

    private final Actions[] scannedActions = {Actions.LIGHT_HIT, Actions.SHRINK};
    private final Map<Actions, ActionGuiIndex> guiIndex = Map.of(
            Actions.LIGHT_HIT, new ActionGuiIndex(0, 0),
            Actions.MEDIUM_HIT, new ActionGuiIndex(1, 0),
            Actions.HARD_HIT, new ActionGuiIndex(0, 1),
            Actions.DRAW, new ActionGuiIndex(1, 1),
            Actions.PUNCH, new ActionGuiIndex(2, 0),
            Actions.BEND, new ActionGuiIndex(3, 1),
            Actions.UPSET, new ActionGuiIndex(2, 0),
            Actions.SHRINK, new ActionGuiIndex(3, 1));

    private Point leftTop, rightBottom;

    public Recorder(MainGUIInterface gui, NativeListener nativeListener) {
        this.gui = gui;
        this.nativeListener = nativeListener;
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
            keyListener = () -> {
                if (isRecording()) {
                    StopRecording();
                } else {
                    StartRecording();
                }
            };
        }
        if (mouseListener == null) {
            mouseListener = event -> {
                if (!isRecording()) {
                    return;
                }

                if (currentAction != null) {
                    actionValuePointMap.put(
                            currentAction.name, new ActionValuePoint(currentAction, event.getX(), event.getY()));
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
            };
        }
        nativeListener.addKeyPressedListener(65, keyListener); // F7
        nativeListener.addMouseClickedListener(1, mouseListener); // Button 1 (left click)
    }

    @Override
    public void disableListeners() {
        nativeListener.removeKeyPressedListener(65, keyListener); // F7
        nativeListener.removeMouseClickedListener(1, mouseListener); // Button 1 (left click)
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
        if (currentAction == null && leftTop != null && rightBottom != null) {
            FinalizeActions();
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
            actionIterator = Arrays.asList(scannedActions).iterator();
        }
        if (actionIterator.hasNext()) {
            currentAction = actionIterator.next();
            gui.setGuideLabel(GuideLabel.createActionRecordingLabel(currentAction.name));
            return;
        }
        currentAction = null;
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

    private void FinalizeActions() {
        ActionValuePoint first = actionValuePointMap.get(scannedActions[0].name);
        ActionValuePoint last = actionValuePointMap.get(scannedActions[1].name);
        int width = last.posX() - first.posX();
        int height = last.posY() - first.posY();
        double stepX = width / 3.;
        for (Actions currentAction : Actions.values()) {
            var index = guiIndex.get(currentAction);
            int posX = Math.toIntExact(Math.round(index.idxX() * stepX) + first.posX());
            int posY = index.idxY() * height + first.posY();
            actionValuePointMap.put(currentAction.name, new ActionValuePoint(currentAction, posX, posY));
        }
    }

    private void ClearData() {
        actionIterator = null;
        actionValuePointMap.clear();
        leftTop = null;
        rightBottom = null;
        currentAction = null;
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
