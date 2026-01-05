package blacksmith;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import wrappers.ActionValuePoint;

/**
 * Performance tests for the Planner class using SPFA (Bellman-Ford optimization).
 *
 * These tests verify that the algorithm:
 * - Completes within reasonable time bounds
 * - Explores a reasonable number of positions
 * - Finds optimal or near-optimal solutions
 */
public class PlannerPerformanceTest {

    private List<ActionValuePoint> actions;

    @BeforeEach
    void setUp() {
        actions = new ArrayList<>();
    }

    private void addAllActions() {
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        actions.add(new ActionValuePoint(Actions.BEND, 0, 0));
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        actions.add(new ActionValuePoint(Actions.SHRINK, 0, 0));
        actions.add(new ActionValuePoint(Actions.LIGHT_HIT, 0, 0));
        actions.add(new ActionValuePoint(Actions.MEDIUM_HIT, 0, 0));
        actions.add(new ActionValuePoint(Actions.HARD_HIT, 0, 0));
        actions.add(new ActionValuePoint(Actions.DRAW, 0, 0));
    }

    /**
     * Provides test data for parameterized performance tests.
     * Format: (target, maxIterations, maxPositions, maxTimeMs, maxSteps)
     */
    private static Stream<Arguments> performanceTestCases() {
        return Stream.of(
                Arguments.of(50, 1500, 200, 5, 10),
                Arguments.of(100, 1500, 400, 5, 10),
                Arguments.of(200, 3000, 800, 10, 20),
                Arguments.of(2000, 32000, 4000, 15, 130));
    }

    @ParameterizedTest(name = "Target {0}: should complete in < {1} iterations")
    @MethodSource("performanceTestCases")
    void testPerformance_ParameterizedTargets(
            int target, int maxIterations, int maxPositions, int maxTimeMs, int maxSteps) {

        addAllActions();
        Planner planner = new Planner(actions);

        long startTime = System.nanoTime();
        List<ActionValuePoint> result = planner.plan(target, target);
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        // Verify solution found
        assertNotNull(result, "Should find solution for target " + target);

        // Verify performance metrics
        int iterations = planner.getIterationCount();
        int positionsExplored = planner.getPositionsExplored();

        System.out.println(String.format(
                "Target %d: %d iterations (limit: %d), %d positions (limit: %d), %dms (limit: %dms)",
                target, iterations, maxIterations, positionsExplored, maxPositions, durationMs, maxTimeMs));

        assertTrue(
                positionsExplored < maxPositions,
                String.format("Should explore < %d positions, explored: %d", maxPositions, positionsExplored));
        assertTrue(
                iterations < maxIterations,
                String.format("Should take < %d iterations, took: %d", maxIterations, iterations));
        assertTrue(
                durationMs < maxTimeMs, String.format("Should complete in < %dms, took: %dms", maxTimeMs, durationMs));

        // Verify solution correctness
        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(target, sum, "Solution sum should equal target");

        // Verify solution is reasonably optimal
        assertTrue(
                result.size() <= maxSteps,
                String.format("Should find solution in <= %d steps, found: %d", maxSteps, result.size()));
    }

    @ParameterizedTest(name = "Real-world target {0}")
    @ValueSource(ints = {25, 46, 63, 89, 112, 150})
    void testRealWorldScenarios(int target) {
        // Verify that realistic game scenarios don't hit the iteration limit
        // This tests the fix for maxIterations=100 bug
        addAllActions();
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(target, target);
        assertNotNull(result, "Should find solution for target " + target);

        int iterations = planner.getIterationCount();
        // Old maxIterations=100 would fail for most of these
        assertTrue(
                iterations <= 10000,
                String.format("Iterations (%d) should be well below max for target %d", iterations, target));

        // Verify solution
        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(target, sum, "Solution should equal target " + target);
    }

    @Test
    void testHeuristicEfficiency_Target77() {
        // Compare efficiency with a target that would be problematic for naive BFS
        // Target = 77 (an odd number requiring specific combinations)
        addAllActions();
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(77);

        assertNotNull(result, "Should find solution for target 77");

        int positionsExplored = planner.getPositionsExplored();

        // SPFA should be reasonably efficient
        // Naive BFS might explore 300-500+ positions for target 77
        // SPFA should explore fewer positions
        assertTrue(positionsExplored < 300, "SPFA should be efficient, explored: " + positionsExplored);

        // Verify solution is correct
        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(77, sum);
    }

    @ParameterizedTest(name = "Real-world boundary: target {0}")
    @ValueSource(ints = {0, 1, 10, 50, 90, 99, 100})
    void testRealWorldConstraints_WithinBounds(int target) {
        // In real-world TFC Anvil, the distance must stay within [0, 100]
        // This tests that the algorithm works for all valid targets
        addAllActions();
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(target);

        if (target == 0) {
            assertNotNull(result, "Should return empty path for target 0");
            assertEquals(0, result.size());
        } else {
            assertNotNull(result, "Should find solution for target " + target);

            // Verify solution is correct
            int sum = result.stream().mapToInt(a -> a.action().value).sum();
            assertEquals(target, sum, "Solution should equal target " + target);

            // Verify the path never goes outside [0, 100]
            int position = 0;
            for (ActionValuePoint action : result) {
                position += action.action().value;
                assertTrue(
                        position >= 0 && position <= 100,
                        String.format(
                                "Position %d is outside valid range [0, 100] after action %s",
                                position, action.action().name));
            }
        }
    }

    @Test
    void testRealWorldConstraints_PathStaysInBounds() {
        // Test that for a typical target, the cumulative value never goes negative
        // This verifies the algorithm respects the constraint during pathfinding
        addAllActions();
        Planner planner = new Planner(actions);

        int target = 46; // Typical game target
        List<ActionValuePoint> result = planner.plan(target);

        assertNotNull(result, "Should find solution for target 46");

        // Walk through the path and verify cumulative value stays >= 0
        int cumulativeValue = 0;
        for (ActionValuePoint action : result) {
            cumulativeValue += action.action().value;
            assertTrue(
                    cumulativeValue >= 0,
                    String.format(
                            "Cumulative value %d went negative after action %s",
                            cumulativeValue, action.action().name));
        }

        assertEquals(target, cumulativeValue, "Final cumulative value should equal target");
    }

    @Test
    void testRealWorldConstraints_UpperBoundary() {
        // Test the upper boundary - target 100
        addAllActions();
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(100);

        assertNotNull(result, "Should find solution for target 100");

        // Verify the path to 100
        int position = 0;
        for (ActionValuePoint action : result) {
            position += action.action().value;
            assertTrue(
                    position >= 0 && position <= 100, String.format("Position %d exceeded bounds [0, 100]", position));
        }

        assertEquals(100, position, "Should reach exactly 100");

        // Verify it uses efficient actions (not too many steps)
        assertTrue(result.size() <= 10, "Should find efficient path to 100, used: " + result.size());
    }

    @Test
    void testRealWorldConstraints_LowerBoundary() {
        // Test the lower boundary - target close to 0
        addAllActions();
        Planner planner = new Planner(actions);

        int target = 2; // Minimum positive target (PUNCH = +2)
        List<ActionValuePoint> result = planner.plan(target);

        assertNotNull(result, "Should find solution for target 2");

        // Should be a single PUNCH action
        assertEquals(1, result.size(), "Should use single action for target 2");
        assertEquals(Actions.PUNCH, result.get(0).action());

        // Verify cumulative value is always >= 0
        int cumulative = 0;
        for (ActionValuePoint action : result) {
            cumulative += action.action().value;
            assertTrue(cumulative >= 0, "Cumulative value should never go negative");
        }
    }

    @Test
    void testRealWorldConstraints_PathCannotOvershooting() {
        // Test that if we need target X, we don't overshoot beyond 100 and come back
        addAllActions();
        Planner planner = new Planner(actions);

        int target = 95; // Close to upper bound
        List<ActionValuePoint> result = planner.plan(target);

        assertNotNull(result, "Should find solution for target 95");

        // Walk through path - should never exceed 100
        int position = 0;
        int maxPositionReached = 0;
        for (ActionValuePoint action : result) {
            position += action.action().value;
            maxPositionReached = Math.max(maxPositionReached, position);
        }

        assertTrue(
                maxPositionReached <= 100,
                String.format("Path should not exceed 100, max reached: %d", maxPositionReached));
        assertEquals(95, position, "Should end at target 95");
    }
}
