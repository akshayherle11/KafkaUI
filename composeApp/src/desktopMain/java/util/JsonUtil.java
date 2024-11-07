package util;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class JsonUtil {

    public static String getValueFromPath(JSONObject obj, String path) {
        return valueFromPath(obj, Arrays.asList(path.split("\\.")));
    }

    private static String valueFromPath(JSONObject obj, List<String> path) {
        if (path.size() > 1) {
            String key = path.get(0);
            if (obj.has(key) && obj.get(key).getClass() == JSONObject.class) {
                return valueFromPath(obj.getJSONObject(key), path.subList(1, path.size()));
            } else {
                return null;
            }

        } else {
            return obj.get(path.get(0)).toString();
        }

    }
}
