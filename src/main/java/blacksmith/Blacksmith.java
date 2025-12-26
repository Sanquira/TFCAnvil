package blacksmith;

import gui.GuideLabel;
import gui.MainGUIInterface;
import gui.StatusLabel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import listeners.KeyEventListener;
import listeners.ToggableListeners;
import wrappers.ActionValuePoint;
import wrappers.Recipe;

public class Blacksmith implements ToggableListeners {
    private final MainGUIInterface gui;
    private final NativeListener nativeListener;
    private Robot robot;
    private Scanner scanner;
    private Planner planner;
    private Map<String, ActionValuePoint> actionValuePointMap;
    private Recipe requestedRecipe;
    private KeyEventListener keyListener;

    private Timer repeatClickTimer;
    private ActionValuePoint activeValuePoint;
    private CountDownLatch operationLatch;
    private int lastGreenPos = -1;

    public Blacksmith(MainGUIInterface gui, NativeListener nativeListener) {
        this.gui = gui;
        this.nativeListener = nativeListener;

        repeatClickTimer = new Timer(250, e -> {
            PressButton(activeValuePoint);
        });
        repeatClickTimer.setRepeats(true);
        repeatClickTimer.setCoalesce(true);
        repeatClickTimer.setInitialDelay(0);

        try {
            robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
        } catch (HeadlessException | AWTException e) {
            e.printStackTrace();
        }
        gui.addRecipeChangedListener(newRecipe -> {
            requestedRecipe = newRecipe;
        });

        StateMachine.getInstance().addProgramStateListener(newState -> {
            if (newState.isBlacksmithEnabled()) {
                enableListeners();
            } else {
                disableListeners();
            }
        });
    }

    public void UpdateScanner(Rectangle area) {
        this.scanner = new Scanner(robot, area);
    }

    public void UpdateToolPositions(Map<String, ActionValuePoint> actionValuePointMap) {
        this.actionValuePointMap = actionValuePointMap;
        planner = new Planner(new ArrayList<>(actionValuePointMap.values()));
    }

    public boolean IsReady() {
        return planner != null && scanner != null && requestedRecipe != null;
    }

    private void StartTransaction() {
        try {
            StateMachine.getInstance().setCurrentState(ProgramState.BLACKSMITHING);
            gui.setStatusLabel(StatusLabel.BLACKSMITHING_RUNNING);
            gui.setGuideLabel(GuideLabel.createWaitLabel());

            int distance = scanner.ScanDistance();
            if (distance == 0) {
                return;
            }
            int targetDist = distance + requestedRecipe.startValue();
            if (targetDist < 0) {
                return;
            }
            List<ActionValuePoint> plan = planner.plan(targetDist);
            for (Actions action : requestedRecipe.finishingActions()) {
                plan.add(actionValuePointMap.get(action.name));
            }

            boolean successful = CreateRecipe(plan);
            if (!successful) {
                gui.setStatusLabel(StatusLabel.BLACKSMITHING_ERROR);
            } else {
                gui.setStatusLabel(StatusLabel.BLACKSMITHING_SUCCESS);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            StateMachine.getInstance().setCurrentState(ProgramState.RECORDED);
            gui.setGuideLabel(GuideLabel.createBlacksmithReadyLabel());
        }
    }

    @Override
    public void enableListeners() {
        if (keyListener == null) {
            keyListener = () -> {
                if (IsReady()) {
                    StartTransaction();
                } else {
                    System.err.println("Blacksmith is not ready.");
                }
            };
        }
        nativeListener.addKeyPressedListener(66, keyListener); // F8
    }

    @Override
    public void disableListeners() {
        if (keyListener == null) {
            return;
        }
        nativeListener.removeKeyPressedListener(66, keyListener); // F8
    }

    private boolean CreateRecipe(List<ActionValuePoint> plan) {
        lastGreenPos = -1;
        activeValuePoint = null;

        for (ActionValuePoint action : plan) {
            operationLatch = new CountDownLatch(1);
            activeValuePoint = action;
            repeatClickTimer.restart();
            try {
                boolean await = operationLatch.await(3, TimeUnit.SECONDS);
                if (!await) {
                    repeatClickTimer.stop();
                    System.err.println("Operation is not successful! Interrupting process!");
                    return false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void PressButton(ActionValuePoint point) {
        robot.mouseMove(point.posX(), point.posY());

        int currGreenPos = scanner.ScanGreenPosition();
        System.out.println(lastGreenPos + ", " + currGreenPos);
        if (lastGreenPos != -1 && lastGreenPos != currGreenPos) {
            repeatClickTimer.stop();
            lastGreenPos = currGreenPos;
            operationLatch.countDown(); // No operations after this point
            return;
        }
        lastGreenPos = currGreenPos;

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
