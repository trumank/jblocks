package jblocks;

import java.util.ArrayDeque;
import java.util.Map;

public class Thread {
    Scriptable object;
    ArrayDeque<Frame> stack;
    Frame frame;
    ArrayDeque<Map<String, Value>> paramsStack;
    Map<String, Value> params;
    Object[] script;
    boolean done;
    boolean yield;


    public Thread(Object[] script, Scriptable object) {
        this.script = script;
        this.object = object;
        stack = new ArrayDeque<>();
        paramsStack = new ArrayDeque<>();
        done = true;
    }

    public void start() {
        done = false;
        frame = new Frame();
        frame.script = script;
        stack.clear();
        stack.push(frame);
        paramsStack.clear();
        params = null;
    }

    public void stop() {
        done = true;
    }

    public boolean isRunning() {
        return !done;
    }

    public void tick() {
        yield = false;
        while (!yield && !done) {
            runFrame();
            if (frame.index >= frame.script.length) {
                if (stack.size() > 1) {
                    popFrame();
                } else {
                    done = true;
                }
            }
        }
    }

    public void runFrame() {
        yield = false;
        while (!yield && !done) {
            if (frame.index >= frame.script.length) {
                popFrame();
                return;
            }
            evalCommand((Object[]) frame.script[frame.index]);
        }
    }

    public void evalCommand(Object[] block) {
        String selector = (String) block[0];
        switch (selector) {
        case "call":
            if (frame.init) {
                frame.init = false;
                frame.index++;
            } else {
                frame.init = true;
                pushFrame();
                CustomBlock b = object.getBlock(Value.string(evalArg(block[1])));
                frame.script = b.getScript();
                Value[] t = new Value[block.length - 2];
                for (int i = 0; i < t.length; i++) {
                    t[i] = evalArg(block[i + 2]);
                }
                if (params != null) {
                    paramsStack.push(params);
                }
                params = b.createMap(t);
                frame.yield = b.refreshScreen();
                frame.block = true;
                runFrame();
            }
            break;
        case "wait:elapsed:from:":
            if (!frame.init) {
                frame.timer = new Timer(Value.number(evalArg(block[1])));
                frame.init = true;
                yield();
            }
            if (frame.timer.ended()) {
                frame.init = false;
                frame.index++;
            } else {
                yield();
            }
            break;
        case "doRepeat":
            if (!frame.init) {
                frame.i = (int) Value.number(evalArg(block[1]));
                frame.init = true;
            }
            if (frame.i-- <= 0) {
                frame.index++;
                frame.init = false;
            } else {
                pushFrame();
                frame.script = (Object[]) block[2];
                runFrame();
                yield();
            }
            break;
        case "doForever":
            pushFrame();
            frame.script = (Object[]) block[1];
            runFrame();
            yield();
            break;
        case "doIf":
            if (Value.bool(evalArg(block[1]))) {
                pushFrame();
                frame.script = (Object[]) block[2];
                runFrame();
            }
            frame.index++;
            break;
        case "doIfElse":
            pushFrame();
            frame.script = (Object[]) block[Value.bool(evalArg(block[1])) ? 2 : 3];
            runFrame();
            frame.index++;
            break;
        case "doWaitUntil":
            if (Value.bool(evalArg(block[1]))) {
                frame.index++;
            } else {
                yield();
            }
            break;
        case "doUntil":
            if (Value.bool(evalArg(block[1]))) {
                frame.index++;
            } else {
                pushFrame();
                frame.script = (Object[]) block[2];
                runFrame();
                yield();
            }
            break;
        case "doBroadcastAndWait":
            if (frame.init) {
                if (object.getStage().globalIsEventRunning(frame.msg)) {
                    yield();
                } else {
                    frame.init = false;
                    frame.index++;
                }
            } else {
                frame.init = true;
                frame.msg = "whenIReceive" + Value.string(evalArg(block[1]));
                object.getStage().globalFireEvent(frame.msg);
            }
            break;
        case "stopScripts":
            switch (Value.string((Value) block[1])) {
            case "this script":
                while (stack.size() > 0 && !frame.block) {
                    popFrame();
                }
                if (frame.block) {
                    popFrame();
                    frame.index++;
                }
            }
            break;
        default:
            eval(block);
            frame.index++;
        }
    }

    public void yield() {
        yield = frame.yield;
    }

    public Value evalArg(Object arg) {
        if (arg instanceof Value) {
            return (Value) arg;
        } else if (arg instanceof Object[]) {
            Object[] block = (Object[]) arg;
            if ("getParam".equals(block[0])) {
                return params.get(Value.string((Value) block[1]));
            }
            return eval(block);
        }
        throw new RuntimeException("Unknow block " + arg);
    }

    public Value eval(Object[] block) {
        Value[] args = new Value[block.length - 1];
        for (int i = 1; i < block.length; i++) {
            args[i - 1] = evalArg(block[i]);
        }
        return object.eval((String) block[0], args);
    }

    void pushFrame() {
        Frame old = frame;
        stack.push(frame);
        frame = new Frame();
        frame.yield = old.yield;
    }

    void popFrame() {
        if (frame.block) {
            params = paramsStack.size() > 0 ? paramsStack.pop() : null;
        }
        if (stack.size() > 0) {
            frame = stack.pop();
        } else {
            done = true;
        }
    }

    class Frame {
        public int index = 0; // the block index in the script
        public Object[] script = null; // the script
        public boolean init = false; // flag for initializing special blocks
        public boolean yield = true; // used in custom blocks with screen refresh disabled
        public boolean block = false; // when the thread is inside a custom block
        public int i = 0; // used in repeat block
        public String msg = null; // used in broadcast and wait
        public Timer timer = null; // used in time based blocks
    }
}
