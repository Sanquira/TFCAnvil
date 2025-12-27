package gui;

public enum StatusLabel {
    RECORDING("Recording"),
    NOT_RECORDING("Not recording"),
    BLACKSMITHING_RUNNING("Blacksmithing"),
    RECORDED("Positions recorded"),
    BLACKSMITHING_ERROR("Error! Operation not successful!"),
    BLACKSMITHING_SUCCESS("Job's done!");

    private final String label;

    StatusLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
