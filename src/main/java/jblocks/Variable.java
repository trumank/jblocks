package jblocks;

public class Variable {
    Value value;

    public Variable(Value v) {
        value = v;
    }

    public void setValue(Value v) {
        value = v;
    }

    public Value getValue() {
        return value;
    }
}
