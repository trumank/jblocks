package jblocks;

import java.util.HashMap;
import java.util.Map;

public class CustomBlock {
    String[] params;
    Object[] script;
    boolean refresh;

    public CustomBlock(String[] p, Object[] s, boolean r) {
        params = p;
        script = s;
        refresh = r;
    }

    public Map<String, Value> createMap(Value[] values) {
        Map<String, Value> map = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            map.put(params[i], values[i]);
        }
        return map;
    }

    public Object[] getScript() {
        return script;
    }

    public boolean refreshScreen() {
        return refresh;
    }
}
