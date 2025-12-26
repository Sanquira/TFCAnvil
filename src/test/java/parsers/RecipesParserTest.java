package parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import wrappers.ActionValue;
import wrappers.Recipe;

public class RecipesParserTest {

    @Test
    void parseToMap_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("recipes.json");
        String json = "[{\"name\":\"testRecipe\",\"finishingActions\":[\"act1\",\"act2\"]}]";
        Files.writeString(file, json);

        // Use real ActionValue objects (no mocks)
        Map<String, ActionValue> actionMap = new HashMap<>();
        ActionValue a1 = new ActionValue("act1", 5);
        ActionValue a2 = new ActionValue("act2", 3);
        actionMap.put("act1", a1);
        actionMap.put("act2", a2);

        RecipesParser parser = new RecipesParser(file.toString(), actionMap);
        Map<String, Recipe> result = parser.parseToMap();

        assertNotNull(result, "Parsed recipes map should not be null for known actions");
        assertTrue(result.containsKey("testRecipe"));

        Recipe r = result.get("testRecipe");
        assertEquals("testRecipe", r.name());
        List<ActionValue> finishing = r.finishingActions();
        assertEquals(2, finishing.size());
        // Verify the recipe references the same ActionValue instances from the provided map
        assertSame(a1, finishing.get(0));
        assertSame(a2, finishing.get(1));
    }

    @Test
    void parseToMap_unknownAction_returnsNull(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("recipes_unknown.json");
        String json = "[{\"name\":\"badRecipe\",\"finishingActions\":[\"missing\"]}]";
        Files.writeString(file, json);

        Map<String, ActionValue> actionMap = new HashMap<>(); // no entries

        RecipesParser parser = new RecipesParser(file.toString(), actionMap);
        assertNull(parser.parseToMap(), "Parser should return null when a finishing action is unknown");
    }
}
