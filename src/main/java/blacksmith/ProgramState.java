package blacksmith;

public enum ProgramState {
    INIT(false, false, false),
    STARTED(true, false, true),
    RECORDING(false, false, true),
    RECORDED(true, true, true),
    BLACKSMITHING(false, true, false);

    private final boolean recipesEnabled;
    private final boolean blacksmithEnabled;
    private final boolean recordingEnabled;

    ProgramState(boolean recipesEnabled, boolean blacksmithEnabled, boolean recordingEnabled) {
        this.recipesEnabled = recipesEnabled;
        this.blacksmithEnabled = blacksmithEnabled;
        this.recordingEnabled = recordingEnabled;
    }

    public boolean isRecipesEnabled() {
        return recipesEnabled;
    }

    public boolean isBlacksmithEnabled() {
        return blacksmithEnabled;
    }

    public boolean isRecordingEnabled() {
        return recordingEnabled;
    }
}
