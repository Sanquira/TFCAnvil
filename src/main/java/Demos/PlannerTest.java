package Demos;

import blacksmith.Planner;
import parsers.ActionParser;
import wrappers.ActionValue;
import wrappers.ActionValuePoint;

import java.util.ArrayList;
import java.util.List;

public class PlannerTest {

    public static void main(String[] args) {
        ActionParser actionParser = new ActionParser("action_config.json");
        List<ActionValue> actionValues = actionParser.parse();
        List<ActionValuePoint> actionValuePoints = new ArrayList<>();
        for (ActionValue action : actionValues) {
            actionValuePoints.add(new ActionValuePoint(action, 0, 0));
        }

        Planner planner = new Planner(actionValuePoints);
        List<ActionValuePoint> plan = planner.plan(46);
        for (ActionValuePoint action : plan) {
            System.out.println(action);
        }
    }
}
