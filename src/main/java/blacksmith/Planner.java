package blacksmith;

import wrappers.ActionValuePoint;
import wrappers.PlannerBacktrack;
import wrappers.PlannerNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Planner {

    private final List<ActionValuePoint> actionValuePointList;

    private final Map<Integer, PlannerBacktrack> cameFrom;

    public Planner(List<ActionValuePoint> actionValuePointList) {
        this.actionValuePointList = actionValuePointList;
        cameFrom = new HashMap<>();
    }

    public List<ActionValuePoint> plan(int target) {
        if (target < 0) {
            return null;
        }
        cameFrom.clear();
        Map<Integer, PlannerNode> closedSet = new HashMap<>();
        Map<Integer, PlannerNode> openSet = new HashMap<>();
        openSet.put(0, new PlannerNode(null, 0, 0));

        while (!openSet.isEmpty()) {
            int x = getSmallestF(openSet);
            if (x == target) {
                return reconstructPath(target);
            }
            PlannerNode xData = openSet.remove(x);
            closedSet.put(x, xData);

            for (ActionValuePoint action : actionValuePointList) {
                int n = x + action.actionValue().getValue();
                if (closedSet.containsKey(n)) {
                    continue;
                }
                int currGScore = xData.gValue() + action.actionValue().getValue();
                if (currGScore < 0) {
                    continue;
                }
                if (!openSet.containsKey(n) || currGScore < openSet.get(n).gValue()) {
                    cameFrom.put(n, new PlannerBacktrack(action, x));
                    openSet.put(n, new PlannerNode(action, currGScore, xData.fValue() + 1));
                }
            }
        }
        System.err.println("Planner could not find solution");
        return null;
    }

    private int getSmallestF(Map<Integer, PlannerNode> openSet) {
        int smallest = Integer.MAX_VALUE;
        int smallestKey = 0;
        for (Map.Entry<Integer, PlannerNode> entry : openSet.entrySet()) {
            if (entry.getValue().fValue() < smallest) {
                smallest = entry.getValue().fValue();
                smallestKey = entry.getKey();
            }
        }
        return smallestKey;
    }

    private List<ActionValuePoint> reconstructPath(int currNode) {
        if (cameFrom.containsKey(currNode)) {
            List<ActionValuePoint> p = reconstructPath(cameFrom.get(currNode).cameFrom());
            p.add(cameFrom.get(currNode).actionValuePoint());
            return p;
        } else {
            return new ArrayList<>();
        }
    }
}
