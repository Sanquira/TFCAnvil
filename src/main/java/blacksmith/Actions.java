package blacksmith;

/**
 * Enum representing actions from action_config.json.
 * Each enum constant has a label (original name) and value.
 */
public enum Actions {
    PUNCH("Punch", 2),
    BEND("Bend", 7),
    UPSET("Upset", 13),
    SHRINK("Shrink", 16),
    LIGHT_HIT("Light Hit", -3),
    MEDIUM_HIT("Medium Hit", -6),
    HARD_HIT("Hard Hit", -9),
    DRAW("Draw", -15);

    public final String name;
    public final int value;

    Actions(String label, int value) {
        this.name = label;
        this.value = value;
    }
}
