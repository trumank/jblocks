package jblocks;

import com.fasterxml.jackson.databind.JsonNode;

public class Value {
    enum Type {STRING, NUMBER, BOOLEAN};

    Type type;

    String string;
    double number;
    boolean bool;

    public Value(JsonNode node) {
        if (node.isTextual()) {
            type = Type.STRING;
            string = node.textValue();
        } else if (node.isNumber()) {
            type = Type.NUMBER;
            number = node.doubleValue();
        } else if (node.isBoolean()) {
            type = Type.BOOLEAN;
            bool = node.booleanValue();
        } else {
            throw new RuntimeException("Unknow type of " + node);
        }
    }

    public Value(String s) {
        type = Type.STRING;
        string = s;
    }

    public Value(double n) {
        type = Type.NUMBER;
        number = n;
    }

    public Value(boolean b) {
        type = Type.BOOLEAN;
        bool = b;
    }

    private String asString() {
        switch (type) {
        case STRING:
            return string;
        case NUMBER:
            return string == null ? string = Util.toString(number) : string;
        case BOOLEAN:
            return String.valueOf(bool);
        default:
            return "";
        }
    }

    private double asNumber() {
        switch (type) {
        case STRING:
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                return 0;
            }
        case NUMBER:
            return number;
        case BOOLEAN:
            return 0;
        default:
            return 0;
        }
    }

    private boolean asBoolean() {
        switch (type) {
        case STRING:
            return false;
        case NUMBER:
            return false;
        case BOOLEAN:
            return bool;
        default:
            return false;
        }
    }

    @Override
    public String toString() {
        return asString();
    }

    public boolean equals(Value other) {
        return equals(other, true);
    }

    public boolean equalsWithCase(Value other) {
        return equals(other, false);
    }

    @SuppressWarnings("incomplete-switch")
    public boolean equals(Value other, boolean ignoreCase) {
        if (other == null) {
            return false;
        }
        switch (type) {
        case STRING:
            return ignoreCase ? string.equalsIgnoreCase(other.toString()) : string.equals(other.toString());
        case NUMBER:
            switch (other.type) {
            case NUMBER:
                return number == other.number; // implement epsilon range?
            case BOOLEAN:
                return false;
            }
        case BOOLEAN:
            switch (other.type) {
            case BOOLEAN:
                return bool == other.bool;
            }
        }
        return other.equals(this);
    }

    public static String string(Value v) {
        if (v != null) {
            return v.asString();
        }
        return "";
    }

    public static double number(Value v) {
        if (v != null) {
            return v.asNumber();
        }
        return 0;
    }

    public static boolean bool(Value v) {
        if (v != null) {
            return v.asBoolean();
        }
        return false;
    }
}
