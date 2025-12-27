package Demos;

import parsers.ActionParser;
import wrappers.ActionValue;

import java.util.Map;

public class ActionParserTest {

    public static void main(String[] args) {
        ActionParser actionParser = new ActionParser("action_config.json");
        Map<String, ActionValue> actionValueMap = actionParser.parseToMap();
        for (ActionValue action : actionValueMap.values()) {
            System.out.println(action);
        }
    }
}
