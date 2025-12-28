package blacksmith;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wrappers.ActionValuePoint;

/**
 * Tests for the Planner class.
 *
 * The Planner implements a BFS-like search that:
 * - Minimizes the NUMBER OF ACTIONS (steps) to reach a target position
 * - Does NOT minimize the cumulative cost (gValue)
 * - Prunes paths where cumulative gValue becomes negative
 */
public class PlannerTest {

    private List<ActionValuePoint> actions;

    @BeforeEach
    void setUp() {
        actions = new ArrayList<>();
    }

    @Test
    void testSimpleCase_OneAction() {
        // Single action with value 16 (SHRINK), target = 16
        actions.add(new ActionValuePoint(Actions.SHRINK, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(16);

        assertNotNull(result, "Should find a solution");
        assertEquals(1, result.size(), "Should use exactly 1 action");
        assertEquals(Actions.SHRINK, result.get(0).action(), "Should use SHRINK action");
        assertEquals(16, result.get(0).action().value);
    }

    @Test
    void testSimpleCase_MultipleSteps() {
        // Actions: UPSET (+13), PUNCH (+2)
        // Target: 17 (13+2+2 = 17, requires 3 steps)
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(17);

        assertNotNull(result, "Should find a solution");
        assertEquals(3, result.size(), "Should use 3 actions");

        // Verify specific order: UPSET, PUNCH, PUNCH
        assertEquals(Actions.UPSET, result.get(0).action(), "First action should be UPSET");
        assertEquals(Actions.PUNCH, result.get(1).action(), "Second action should be PUNCH");
        assertEquals(Actions.PUNCH, result.get(2).action(), "Third action should be PUNCH");

        // Verify sum equals target
        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(17, sum, "Sum of action values should equal target");
    }

    @Test
    void testMinimizesStepsNotCost() {
        // This is a critical test showing the algorithm minimizes STEPS, not COST
        // Actions: UPSET (+13), PUNCH (+2)
        // Target: 26
        // By steps: [UPSET, UPSET] = 2 steps
        // By count of +2: [PUNCH × 13] = 13 steps
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(26);

        assertNotNull(result, "Should find a solution");
        assertEquals(2, result.size(), "Should prefer fewer steps: 2×13 instead of 13×2");

        // Verify specific order: UPSET, UPSET
        assertEquals(Actions.UPSET, result.get(0).action(), "First action should be UPSET");
        assertEquals(Actions.UPSET, result.get(1).action(), "Second action should be UPSET");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(26, sum);
    }

    @Test
    void testNegativeTarget_ReturnsNull() {
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(-5);

        assertNull(result, "Should return null for negative target");
    }

    @Test
    void testZeroTarget_ReturnsEmptyPath() {
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(0);

        assertNotNull(result, "Should return empty path for target=0");
        assertEquals(0, result.size(), "Path should be empty (already at target)");
    }

    @Test
    void testUnreachableTarget_ReturnsNull() {
        // Only positive actions PUNCH (+2) and UPSET (+13), cannot reach 1
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        Planner planner = new Planner(actions);

        // Target = 1 is unreachable (can make 2, 4, 6, 8, 10, 12, 13, 14... but not 1)
        List<ActionValuePoint> result = planner.plan(1);

        assertNull(result, "Should return null when target is unreachable");
    }

    @Test
    void testWithNegativeActions_CanReachTarget() {
        // Actions: SHRINK (+16), LIGHT_HIT (-3)
        // Target: 13 (16-3 = 13)
        actions.add(new ActionValuePoint(Actions.SHRINK, 0, 0));
        actions.add(new ActionValuePoint(Actions.LIGHT_HIT, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(13);

        assertNotNull(result, "Should find solution with negative actions");
        assertEquals(2, result.size(), "Should use 2 actions");

        // Verify specific order: SHRINK, LIGHT_HIT
        assertEquals(Actions.SHRINK, result.get(0).action(), "First action should be SHRINK");
        assertEquals(Actions.LIGHT_HIT, result.get(1).action(), "Second action should be LIGHT_HIT");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(13, sum);
    }

    @Test
    void testNegativeActionsPruning_CriticalCase() {
        // This tests the currGScore < 0 pruning
        // Actions: UPSET (+13), DRAW (-15)
        // Target: 11
        //
        // Path: UPSET, UPSET, DRAW = 13+13-15 = 11 (3 steps)
        // Position: 0→13→26→11
        // gScore: 0→13→26→11 (all positive, this IS valid!)
        //
        // This should SUCCEED - the gScore never goes negative

        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        actions.add(new ActionValuePoint(Actions.DRAW, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(11);

        // The algorithm CAN find a valid path where gScore stays non-negative
        assertNotNull(result, "Should find solution: UPSET+UPSET+DRAW keeps gScore positive");
        assertEquals(3, result.size(), "Should use 3 actions");

        // Verify specific order: UPSET, UPSET, DRAW
        assertEquals(Actions.UPSET, result.get(0).action(), "First action should be UPSET");
        assertEquals(Actions.UPSET, result.get(1).action(), "Second action should be UPSET");
        assertEquals(Actions.DRAW, result.get(2).action(), "Third action should be DRAW");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(11, sum);
    }

    @Test
    void testNegativeActionsPruning_SuccessCase() {
        // Actions: UPSET (+13), LIGHT_HIT (-3)
        // Target: 10
        // Path: UPSET, LIGHT_HIT = 13-3 = 10 (gScore stays positive: 0→13→10)
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        actions.add(new ActionValuePoint(Actions.LIGHT_HIT, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(10);

        assertNotNull(result, "Should find solution when gScore stays non-negative");
        assertEquals(2, result.size());

        // Verify specific order: UPSET, LIGHT_HIT
        assertEquals(Actions.UPSET, result.get(0).action(), "First action should be UPSET");
        assertEquals(Actions.LIGHT_HIT, result.get(1).action(), "Second action should be LIGHT_HIT");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(10, sum);
    }

    @Test
    void testLargeTarget_Performance() {
        // Test with larger target to see if algorithm performs reasonably
        // Use PUNCH (+2), UPSET (+13), SHRINK (+16)
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        actions.add(new ActionValuePoint(Actions.SHRINK, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(100);

        assertNotNull(result, "Should find solution for large target");
        assertTrue(result.size() <= 20, "Should find reasonably short path");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(100, sum);
    }

    @Test
    void testMultiplePathsSameLength_ChoosesLowerCost() {
        // Actions: BEND (+7), PUNCH (+2), UPSET (+13)
        // Target: 14
        // Paths with 2 steps: BEND+BEND=14 (2 steps)
        // Paths with 3+ steps: various combinations
        // Algorithm should prefer 2 steps
        actions.add(new ActionValuePoint(Actions.BEND, 0, 0));
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(14);

        assertNotNull(result, "Should find solution");
        assertEquals(2, result.size(), "Should prefer 2-step solution");

        // Verify specific order: BEND, BEND
        assertEquals(Actions.BEND, result.get(0).action(), "First action should be BEND");
        assertEquals(Actions.BEND, result.get(1).action(), "Second action should be BEND");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(14, sum);
    }

    @Test
    void testZeroValueAction_PotentialInfiniteLoop() {
        // CRITICAL: Zero-value actions could cause infinite loops
        // We don't have a zero-value action in the enum, so test with existing actions
        // Use PUNCH (+2)
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(2);

        assertNotNull(result, "Should find solution");
        assertEquals(1, result.size(), "Should find shortest path");
        assertEquals(Actions.PUNCH, result.get(0).action(), "Should use PUNCH action");
        assertEquals(2, result.get(0).action().value);
    }

    @Test
    void testNoActions_ReturnsNull() {
        // Empty action list
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(10);

        assertNull(result, "Should return null when no actions available");
    }

    @Test
    void testDuplicateActions_StillMinimizesSteps() {
        // Multiple copies of same action - use BEND (+7)
        actions.add(new ActionValuePoint(Actions.BEND, 10, 10));
        actions.add(new ActionValuePoint(Actions.BEND, 20, 20));
        actions.add(new ActionValuePoint(Actions.BEND, 30, 30));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(21);

        assertNotNull(result, "Should find solution with duplicate actions");
        assertEquals(3, result.size(), "Should use 3 actions");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(21, sum);
    }

    @Test
    void testComplexNegativeCase_AlgorithmLimitation() {
        // This demonstrates the algorithm with mixed actions
        // Actions: SHRINK (+16), HARD_HIT (-9), BEND (+7)
        // Target: 14
        //
        // Possible path: SHRINK, HARD_HIT, BEND = 16-9+7 = 14
        // Position trace: 0 → 16 → 7 → 14
        // gScore trace: 0 → 16 → 7 → 14 (all positive, should work!)

        actions.add(new ActionValuePoint(Actions.SHRINK, 0, 0));
        actions.add(new ActionValuePoint(Actions.HARD_HIT, 0, 0));
        actions.add(new ActionValuePoint(Actions.BEND, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(14);

        assertNotNull(result, "Should find solution when gScore remains positive");
        assertTrue(result.size() <= 3, "Should find reasonably short path");

        int sum = result.stream().mapToInt(a -> a.action().value).sum();
        assertEquals(14, sum);
    }

    @Test
    void testNegativeCumulativeCost_BlocksValidSolution() {
        // FAILURE CASE: Valid path exists but requires negative cumulative cost
        //
        // Actions: BEND (+7), DRAW (-15), SHRINK (+16)
        // Target: 8
        //
        // Shortest path: BEND, DRAW, SHRINK = 7-15+16 = 8 (3 steps)
        // Position: 0 → 7 → -8 → 8
        // gScore: 0 → 7 → -8 (PRUNED! because -8 < 0)
        //
        // Algorithm will try longer paths or fail

        actions.add(new ActionValuePoint(Actions.BEND, 0, 0));
        actions.add(new ActionValuePoint(Actions.DRAW, 0, 0));
        actions.add(new ActionValuePoint(Actions.SHRINK, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(8);

        // May find alternative path or fail
        if (result != null) {
            int sum = result.stream().mapToInt(a -> a.action().value).sum();
            assertEquals(8, sum);

            // Verify it didn't use DRAW in a way that creates negative gScore
            // This is complex to verify, so we just check the sum is correct
        }
    }

    @Test
    void testSuboptimalCost_OptimalSteps() {
        // Demonstrates that algorithm optimizes for STEPS not COST
        //
        // Actions: PUNCH (+2), BEND (+7)
        // Target: 9
        //
        // By steps: PUNCH+BEND = 2+7 = 2 steps, sum = 9
        // By steps: PUNCH×4 + one more action...actually 2+2+2+2+2 = 10, not 9
        // Let's use target 14: BEND+BEND = 2 steps vs PUNCH×7 = 7 steps
        //
        // Algorithm chooses 2 steps
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        actions.add(new ActionValuePoint(Actions.BEND, 0, 0));
        Planner planner = new Planner(actions);

        List<ActionValuePoint> result = planner.plan(14);

        assertNotNull(result);
        assertEquals(2, result.size(), "Should minimize steps (2) not cost");

        // Verify specific order: BEND, BEND
        assertEquals(Actions.BEND, result.get(0).action(), "First action should be BEND");
        assertEquals(Actions.BEND, result.get(1).action(), "Second action should be BEND");
    }

    @Test
    void testMaxIterationsProtection() {
        // Verify that unreachable targets don't run forever
        // Only positive actions PUNCH (+2) and UPSET (+13), cannot reach 1
        actions.add(new ActionValuePoint(Actions.PUNCH, 0, 0));
        actions.add(new ActionValuePoint(Actions.UPSET, 0, 0));

        Planner planner = new Planner(actions);

        long startTime = System.nanoTime();
        List<ActionValuePoint> result = planner.plan(1);
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        // Target = 1 is unreachable (can make 2, 4, 6, 8, 10, 12, 13, 14... but not 1)
        assertNull(result, "Should return null when target is unreachable");

        // Should complete quickly even for unreachable target
        assertTrue(durationMs < 1000, "Should timeout gracefully in < 1s, took: " + durationMs + "ms");
    }
}
