package Demos;

import blacksmith.Actions;
import blacksmith.Planner;
import java.util.ArrayList;
import java.util.List;
import wrappers.ActionValuePoint;

public class PlannerTest {

    public static void main(String[] args) {
        // Create action value points from the Actions enum
        List<ActionValuePoint> actionValuePoints = new ArrayList<>();
        for (Actions action : Actions.values()) {
            actionValuePoints.add(new ActionValuePoint(action, 0, 0));
        }

        Planner planner = new Planner(actionValuePoints);
        List<ActionValuePoint> plan = planner.plan(46);

        if (plan != null) {
            for (ActionValuePoint action : plan) {
                System.out.println(action);
            }
        } else {
            System.out.println("No plan found for target 46");
        }
    }
}
