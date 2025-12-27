package wrappers;

public class ActionValue {
    private final int value;
    private final String name;

    public ActionValue(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public ActionValue(ActionValue obj) {
        this.name = obj.name;
        this.value = obj.value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ActionValue{" +
                "actionValue=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
