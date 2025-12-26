package wrappers;

public record ActionValue(String name, int value) {
    public ActionValue(ActionValue obj) {
        this(obj.name(), obj.value());
    }

    @Override
    public String toString() {
        return "ActionValue{" + "actionValue=" + value() + ", name='" + name() + '\'' + '}';
    }
}
