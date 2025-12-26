package parsers;

import static org.junit.jupiter.api.Assertions.*;

import blacksmith.Actions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import wrappers.Recipe;

public class RecipesParserTest {

    @Test
    void parseToMap_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("recipes.json");
        String json = "[{\"name\":\"testRecipe\",\"finishingActions\":[\"Light Hit\",\"Punch\"]}]";
        Files.writeString(file, json);

        RecipesParser parser = new RecipesParser(file.toString());
        var result = parser.parseToMap();

        assertNotNull(result, "Parsed recipes map should not be null for known actions");
        assertTrue(result.containsKey("testRecipe"));

        Recipe r = result.get("testRecipe");
        assertEquals("testRecipe", r.name());
        List<Actions> finishing = r.finishingActions();
        assertEquals(2, finishing.size());
        // Verify the recipe references the Actions enum values
        assertEquals(Actions.LIGHT_HIT, finishing.get(0));
        assertEquals(Actions.PUNCH, finishing.get(1));
    }

    @Test
    void parseToMap_unknownAction_returnsNull(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("recipes_unknown.json");
        String json = "[{\"name\":\"badRecipe\",\"finishingActions\":[\"missing\"]}]";
        Files.writeString(file, json);

        RecipesParser parser = new RecipesParser(file.toString());
        assertNull(parser.parseToMap(), "Parser should return null when a finishing action is unknown");
    }
}
