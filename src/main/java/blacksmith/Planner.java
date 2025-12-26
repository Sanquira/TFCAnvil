package blacksmith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wrappers.ActionValuePoint;
import wrappers.PlannerBacktrack;
import wrappers.PlannerNode;

public class Planner {

    private final List<ActionValuePoint> actionValuePointList;

    private final Map<Integer, PlannerBacktrack> cameFrom;

    // Statistics for performance testing
    private int lastIterationCount = 0;
    private int lastPositionsExplored = 0;

    public Planner(List<ActionValuePoint> actionValuePointList) {
        this.actionValuePointList = actionValuePointList;
        cameFrom = new HashMap<>();
    }

    public int getLastIterationCount() {
        return lastIterationCount;
    }

    public int getLastPositionsExplored() {
        return lastPositionsExplored;
    }

    public List<ActionValuePoint> plan(int target) {
        if (target < 0) {
            return null;
        }
        cameFrom.clear();
        Map<Integer, PlannerNode> closedSet = new HashMap<>();
        Map<Integer, PlannerNode> openSet = new HashMap<>();
        openSet.put(0, new PlannerNode(null, 0, 0));

        // Prevent infinite loops: if we've explored too many positions, target is likely unreachable
        // Calculate reasonable limit based on target and available actions
        // The closed set prevents revisiting, so this is bounded by unique reachable positions
        int maxIterations = Math.max(10000, Math.abs(target) * 100);
        int iterations = 0;

        while (!openSet.isEmpty()) {
            if (++iterations > maxIterations) {
                System.err.println("Planner exceeded max iterations - target likely unreachable");
                lastIterationCount = iterations;
                lastPositionsExplored = closedSet.size();
                return null;
            }

            int x = getSmallestF(openSet);
            if (x == target) {
                lastIterationCount = iterations;
                lastPositionsExplored = closedSet.size();
                return reconstructPath(target);
            }
            PlannerNode xData = openSet.remove(x);
            closedSet.put(x, xData);

            for (ActionValuePoint action : actionValuePointList) {
                int n = x + action.action().value;
                if (closedSet.containsKey(n)) {
                    continue;
                }
                int currGScore = xData.gValue() + action.action().value;
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
        lastIterationCount = iterations;
        lastPositionsExplored = closedSet.size();
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
