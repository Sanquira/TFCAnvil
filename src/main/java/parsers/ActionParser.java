package parsers;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wrappers.ActionValue;

public class ActionParser extends AbstractParser<List<ActionValue>> {
    private static final Type CONFIG_TYPE = new TypeToken<List<ActionValue>>() {}.getType();

    public ActionParser(String filePath) {
        super(filePath);
    }

    @Override
    protected Type getObjectType() {
        return CONFIG_TYPE;
    }

    public Map<String, ActionValue> parseToMap() {
        List<ActionValue> actionValues = super.parse();
        Map<String, ActionValue> actionValueMap = new HashMap<>();
        for (ActionValue action : actionValues) {
            actionValueMap.put(action.name(), action);
        }
        return actionValueMap;
    }
}
