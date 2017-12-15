package jblocks;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

public class List {
    String name;
    java.util.List<Value> items;

    public List(JsonNode config) {
        items = new ArrayList<>();
        for (JsonNode i : config.path("contents")) {
            items.add(new Value(i));
        }
    }

    public void error() {
        // TODO: Display error
    }

    public void add(Value v) {
        items.add(v);
    }

    public void delete(Value i) {
        switch (i.type) {
        case BOOLEAN:
            error();
            return;
        case NUMBER:
            int index = (int) Value.number(i) - 1;
            if (index < 0 || index >= items.size()) {
                error();
                return;
            }
            items.remove(index);
            break;
        case STRING:
            if ("last".equals(i.string)) {
                if (items.size() == 0) {
                    error();
                    return;
                }
                items.remove(items.size() - 1);
            } else if ("all".equals(i.string)) {
                items.clear();
            }
        }
        if (i.type == Value.Type.STRING && i.equals("all")) {
            items.clear();
        }
    }

    public void insert(Value index, Value item) {
        int i = getIndexFromValue(index, 1);
        if (i == -1) {
            error();
            return;
        }
        items.add(i, item);
    }

    public void replace(Value index, Value item) {
        int i = getIndexFromValue(index);
        if (i == -1) {
            error();
            return;
        }
        items.set(i, item);
    }

    public Value get(Value index) {
        int i = getIndexFromValue(index);
        if (i == -1) {
            error();
            return null;
        }
        return items.get(i);
    }

    public Value size() {
        return new Value(items.size());
    }

    public Value contains(Value item) {
        return new Value(items.contains(item));
    }

    public int getIndexFromValue(Value i) {
        return getIndexFromValue(i, 0);
    }

    public int getIndexFromValue(Value i, int o) {
        int index = 0;
        switch (i.type) {
        case BOOLEAN:
            return -1;
        case NUMBER:
            index = (int) Value.number(i) - 1;
            break;
        case STRING:
            if ("last".equals(i.string)) {
                index = items.size() - 1 + o;
            } else if ("random".equals(i.string)) {
                index = (int) (Math.random() * (items.size() + o));
            } else {
                index = (int) Value.number(i) - 1;
            }
            break;
        }
        if (index < 0 || index >= items.size()) {
            return -1;
        }
        return index;
    }
}
