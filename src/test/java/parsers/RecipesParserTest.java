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
    void parse_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("recipes.json");
        String json = "[{\"name\":\"testRecipe\",\"finishingActions\":[\"Light Hit\",\"Punch\"]}]";
        Files.writeString(file, json);

        RecipesParser parser = new RecipesParser(file.toString());
        List<Recipe> result = parser.parse();

        assertNotNull(result, "Parsed recipes list should not be null for known actions");
        assertEquals(1, result.size());
        Recipe r = result.get(0);
        assertEquals("testRecipe", r.name());
        List<Actions> finishing = r.finishingActions();
        assertEquals(2, finishing.size());
        assertEquals(Actions.LIGHT_HIT, finishing.get(0));
        assertEquals(Actions.PUNCH, finishing.get(1));
    }

    @Test
    void parse_success_fromResource() throws Exception {
        RecipesParser parser = new RecipesParser("recipes.json");
        List<Recipe> result = parser.parse();
        assertNotNull(result);
    }

    @Test
    void parse_unknownAction_returnsNull(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("recipes_unknown.json");
        String json = "[{\"name\":\"badRecipe\",\"finishingActions\":[\"missing\"]}]";
        Files.writeString(file, json);

        RecipesParser parser = new RecipesParser(file.toString());
        assertNull(parser.parse(), "Parser should return null when a finishing action is unknown");
    }
}
