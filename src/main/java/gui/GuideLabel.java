package gui;

public class GuideLabel {

    private String label;

    private GuideLabel(String label) {
        this.label = label;
    }

    public static GuideLabel createRecordGuide() {
        return new GuideLabel("Press F7 to start recording.");
    }

    public static GuideLabel createActionRecordingLabel(String actionName) {
        return new GuideLabel("Press " + actionName + " button.");
    }

    public static GuideLabel createLeftTopPositionRecordingLabel() {
        return new GuideLabel("Select left top position.");
    }

    public static GuideLabel createRightBottomPositionRecordingLabel() {
        return new GuideLabel("Select right bottom position.");
    }

    public static GuideLabel createWaitLabel() {
        return new GuideLabel("Working! Wait for finish.");
    }

    public static GuideLabel createBlacksmithReadyLabel() {
        return new GuideLabel("Press F8 to start working.");
    }

    public String getLabel() {
        return label;
    }
}
