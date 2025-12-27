package parsers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;

public abstract class AbstractParser<T> {

    Gson g;
    String filePath;

    public AbstractParser(String filePath) {
        this.filePath = filePath;
        this.g = new Gson();
    }

    protected InputStream openInputStream(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists() && file.isFile() && file.canRead()) {
                return new java.io.FileInputStream(file);
            }
        } catch (Exception e) {
            System.err.println("Error opening file: " + fileName);
            e.printStackTrace();
        }
        System.err.println("recipes.json not found, using internal recipes.json: " + fileName);
        // Fallback: try to load from resources (classpath)
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (resourceStream != null) {
            return resourceStream;
        }
        System.err.println("File does not exist: " + fileName + " (also not found in resources)");
        return null;
    }

    protected JsonReader readJson(InputStream in) {
        return new JsonReader(new java.io.InputStreamReader(in));
    }

    protected abstract Type getObjectType();

    public T parse() {
        InputStream in = openInputStream(filePath);
        if (in == null) {
            return null;
        }
        JsonReader jsonReader = readJson(in);
        try {
            return g.fromJson(jsonReader, getObjectType());
        } catch (com.google.gson.JsonParseException e) {
            // Print the parser-provided message (e.g. Unknown finishing action...) and return null
            System.err.println(e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
