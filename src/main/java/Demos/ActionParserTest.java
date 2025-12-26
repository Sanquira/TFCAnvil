package Demos;

import blacksmith.Actions;

public class ActionParserTest {

    public static void main(String[] args) {
        // Actions are now defined as an enum, not parsed from JSON
        System.out.println("Available Actions:");
        for (Actions action : Actions.values()) {
            System.out.println(action.name + ": " + action.value);
        }
    }
}
