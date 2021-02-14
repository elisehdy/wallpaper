package tree.hacks.wallpaper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataPasser implements Serializable {

    Map<String, Object> dataMap;

    public DataPasser() {
        dataMap = new HashMap<>();
    }

    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    public Object get(String key) {
        return dataMap.get(key);
    }
}
