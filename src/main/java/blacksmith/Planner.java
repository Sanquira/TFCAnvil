package blacksmith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wrappers.ActionValuePoint;

public class Planner {

    private final List<ActionValuePoint> actionValuePointList;

    // Track path: position -> (action, previousPosition)
    private final Map<Integer, PathStep> paths;

    // Statistics for performance testing
    private int lastIterationCount = 0;
    private int lastPositionsExplored = 0;

    public Planner(List<ActionValuePoint> actionValuePointList) {
        this.actionValuePointList = actionValuePointList;
        paths = new HashMap<>();
    }

    public int getLastIterationCount() {
        return lastIterationCount;
    }

    public int getLastPositionsExplored() {
        return lastPositionsExplored;
    }

    public List<ActionValuePoint> plan(int target, int maxValue) {
        if (target < 0) {
            return null;
        }

        if (target == 0) {
            lastIterationCount = 0;
            lastPositionsExplored = 1;
            return new ArrayList<>();
        }

        paths.clear();

        Map<Integer, Integer> visited = new HashMap<>();
        visited.put(0, 0);

        List<Integer> currentLevel = new ArrayList<>();
        currentLevel.add(0);

        int maxSteps = Math.max(100, Math.abs(target) / 2);
        lastIterationCount = 0;

        for (int step = 0; step < maxSteps; step++) {
            if (currentLevel.isEmpty()) {
                break;
            }

            List<Integer> nextLevel = new ArrayList<>();

            for (Integer position : currentLevel) {
                int currentCumulative = visited.get(position);

                for (ActionValuePoint action : actionValuePointList) {
                    lastIterationCount++;

                    int newPosition = position + action.action().value;
                    int newCumulative = currentCumulative + action.action().value;

                    if (newCumulative < 0 || newCumulative > maxValue) {
                        continue;
                    }

                    if (!visited.containsKey(newPosition)) {
                        visited.put(newPosition, newCumulative);
                        paths.put(newPosition, new PathStep(action, position));

                        if (newPosition == target) {
                            lastPositionsExplored = visited.size();
                            return reconstructPath(target);
                        }

                        nextLevel.add(newPosition);
                    }
                }
            }

            currentLevel = nextLevel;
        }

        System.err.println("Planner could not find solution");
        lastPositionsExplored = visited.size();
        return null;
    }

    public List<ActionValuePoint> plan(int target) {
        return this.plan(target, 100);
    }

    private List<ActionValuePoint> reconstructPath(int target) {
        List<ActionValuePoint> result = new ArrayList<>();
        int current = target;

        while (paths.containsKey(current)) {
            PathStep step = paths.get(current);
            result.add(step.action);
            current = step.previousPosition;
        }

        // Reverse to get forward path
        Collections.reverse(result);
        return result;
    }

    private record PathStep(ActionValuePoint action, int previousPosition) {}
}
