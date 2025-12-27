package parsers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

abstract public class AbstractParser<T> {

    Gson g;
    String filePath;

    public AbstractParser(String filePath) {
        this.filePath = filePath;
        this.g = new Gson();
    }

    protected File openFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("File does not exist: " + fileName);
            return null;
        }
        if (!file.isFile()) {
            System.err.println(fileName + " is not file!");
            return null;
        }
        if (!file.canRead()) {
            System.err.println("Cannot read file: " + fileName);
            return null;
        }
        return file;
    }

    protected JsonReader readJson(File file) {
        try {
            return new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    abstract protected Type getObjectType();

    public T parse() {
        File file = openFile(filePath);
        if (file == null) {
            return null;
        }
        JsonReader jsonReader = readJson(file);
        if (jsonReader == null) {
            return null;
        }
        return g.fromJson(jsonReader, getObjectType());
    }
}
