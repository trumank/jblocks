package jblocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.ShaderSourceException;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Scriptable {
    String name;
    java.util.List<Costume> costumes;
    int costumeIndex;
    org.jsfml.graphics.Sprite sprite;
    Map<String, Variable> variables;
    Map<String, List> lists;
    java.util.List<Thread> threads;
    Map<String, java.util.List<Thread>> events;
    Map<String, CustomBlock> blocks;
    Map<String, Float> effects;

    public Scriptable(JsonNode config) {
        if (config.get("objName") != null) {
            name = config.path("objName").asText();
        }
        costumes = new ArrayList<>();
        for (JsonNode costume : config.path("costumes")) {
            costumes.add(new Costume(costume));
        }
        sprite = new org.jsfml.graphics.Sprite();
        costumeIndex = config.path("currentCostumeIndex").asInt();

        variables = new HashMap<>();
        for (JsonNode variable : config.path("variables")) {
            variables.put(variable.path("name").asText(), new Variable(new Value(variable.path("value"))));
        }
        lists = new HashMap<>();
        for (JsonNode list : config.path("lists")) {
            lists.put(list.path("listName").asText(), new List(list));
        }

        threads = new ArrayList<>();
        events = new HashMap<>();
        blocks = new HashMap<>();
        for (JsonNode script : config.path("scripts")) {
            buildScript(script.get(2));
        }
        effects = new HashMap<>();
        resetFilters();
    }

    static final java.util.List<String> HATS = Arrays.asList("procDef", "whenGreenFlag", "whenKeyPressed", "whenClicked", "whenSceneStarts", "whenSensorGreaterThan", "whenIReceive");

    Object[] buildScript(JsonNode config) {
        java.util.List<Object> script = new ArrayList<>(config.size());
        Object[] hat = null;
        for (JsonNode block : config) {
            if (HATS.contains(block.get(0).textValue())) {
                hat = buildBlock(block);
            } else {
                script.add(buildBlock(block));
            }
        }
        Object[] array = new Object[script.size()];
        array = script.toArray(array);
        if (hat != null) {
            switch ((String) hat[0]) {
            case "procDef":
                blocks.put(hat[1].toString(), new CustomBlock((String[]) hat[2], array, !Value.bool((Value) hat[4])));
                break;
            case "whenGreenFlag":
                addEvent("whenGreenFlag", array);
                break;
            case "whenKeyPressed":
                addEvent("whenKeyPressed" + hat[1], array);
                break;
            case "whenClicked":
                addEvent("whenClicked", array);
                break;
            case "whenSceneStarts":
                addEvent("whenSceneStarts" + hat[1], array);
                break;
            case "whenSensorGreaterThan":
                addEvent("whenSensorGreaterThan" + hat[1], array);
                break;
            case "whenIReceive":
                addEvent("whenIReceive" + hat[1], array);
                break;
            }
        }
        return array;
    }

    void addEvent(String event, Object[] script) {
        if (events.get(event) == null) {
            events.put(event, new ArrayList<Thread>());
        }
        Thread th = new Thread(script, this);
        threads.add(th);
        events.get(event).add(th);
    }

    Object[] buildBlock(JsonNode config) {
        Object[] block = new Object[config.size()];
        boolean procDef = config.get(0).asText().equals("procDef");
        for (int j = 0; j < config.size(); j++) {
            if (j == 0) {
                block[0] = config.get(0).asText();
            } else {
                JsonNode arg = config.get(j);
                if (arg.isArray()) {
                    if (procDef) {
                        if (j == 2) {
                            ArrayList<String> params = new ArrayList<>(arg.size());
                            for (JsonNode p : arg) {
                                params.add(p.asText());
                            }
                            block[j] = params.toArray(new String[params.size()]);
                            continue;
                        } else if (j == 3) {
                            continue;
                        }
                    }
                    block[j] = (arg.size() != 0 && arg.get(0).isArray()) ? buildScript(arg) : buildBlock(arg);
                } else if (arg.isNull()) {
                    block[j] = new Object[0];
                } else {
                    block[j] = new Value(arg);
                }
            }
        }
        return block;
    }

    CustomBlock getBlock(String block) {
        return blocks.get(block);
    }

    public void tick() {
        for (Thread th : threads) {
            th.tick();
        }
    }

    public void fireEvent(String event) {
        java.util.List<Thread> ths = events.get(event);
        if (ths == null) {
            return;
        }
        for (Thread th : ths) {
            th.start();
        }
    }

    public boolean isEventRunning(String event) {
        java.util.List<Thread> ths = events.get(event);
        if (ths == null) {
            return false;
        }
        for (Thread th : ths) {
            if (!th.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public abstract Stage getStage();

    public Variable getVariable(String name) {
        return variables.get(name);
    }

    public List getList(String name) {
        return lists.get(name);
    }

    public void setCostumeIndex(int index) {
        costumeIndex = index;
        updateSprite();
    }

    public void updateSprite() {
        Costume costume = getCostume();
        sprite.setTexture(costume.getTexture());
        sprite.setOrigin(costume.getCenter());
        float f = 1 / getCostume().getResolution();
        sprite.setScale(f, f);
    }

    public Costume getCostume() {
        return costumes.get(costumeIndex);
    }

    public boolean switchToCostume(String name) {
        for (int i = 0; i < costumes.size(); i++) {
            if (costumes.get(i).getName().equals(name)) {
                setCostumeIndex(i);
                return true;
            }
        }
        return false;
    }

    public void switchToCostume(Value costume) {
        if (costume.type == Value.Type.STRING) {
            if (switchToCostume(Value.string(costume))) {
                return;
            }
        }
        setCostumeIndex(Util.mod((int) Value.number(costume) - 1, costumes.size()));
    }

    public Value getAttribute(String attribute) {
        switch (attribute) {
        case "volume":
            throw new RuntimeException("Unimplemented");
        default:
            return getVariable(attribute).getValue();
        }
    }

    public void resetFilters() {
        effects.put("mosaic", 0f);
        effects.put("fisheye", 0f);
        effects.put("whirl", 0f);
        effects.put("ghost", 0f);
    }

    public Value eval(String selector, Value[] args) {
        switch (selector) {
        // EVENTS BEGIN
        case "broadcast:":
            getStage().globalFireEvent("whenIReceive" + args[0]);
            return null;
        // EVENTS END

        // LOOKS BEGIN
        case "lookLike:":
            switchToCostume(args[0]);
            return null;
        case "startScene":
            switchToCostume(args[0]);
            getStage().globalFireEvent("whenSceneStarts" + args[0]);
            return null;
        case "setGraphicEffect:to:":
            effects.put(Value.string(args[0]), (float) Value.number(args[1]));
            return null;
        case "filterReset":
            resetFilters();
            return null;
        case "costumeIndex":
            return new Value(costumeIndex + 1);
        // LOOKS END

        // SENSING BEGIN
        case "keyPressed:":
            return new Value(getStage().isKeyPressed(Value.string(args[0])));
        case "mousePressed":
            return new Value(getStage().getMouseDown());
        case "mouseX":
            return new Value(getStage().getMouseX());
        case "mouseY":
            return new Value(getStage().getMouseY());
        case "timer":
            return new Value(getStage().getTimer().elapsed());
        case "timerReset":
            getStage().getTimer().reset();
            return null;
        case "getAttribute:of:":
            return getStage().getObject(Value.string(args[1])).getAttribute(Value.string(args[0]));
        // SENSING END

        // PEN BEGIN
        case "clearPenTrails":
            getStage().pen.clear(Color.TRANSPARENT);
            return null;
        // PEN END

        // OPERATORS BEGIN
        case "+":
            return new Value(Value.number(args[0]) + Value.number(args[1]));
        case "-":
            return new Value(Value.number(args[0]) - Value.number(args[1]));
        case "*":
            return new Value(Value.number(args[0]) * Value.number(args[1]));
        case "/":
            return new Value(Value.number(args[0]) / Value.number(args[1]));
        case "randomFrom:to:":
            double a = Value.number(args[0]);
            double b = Value.number(args[1]);
            boolean i = a == Math.rint(a) && b == Math.rint(b);
            double r = Math.random() * (b - a) + a;
            return new Value(i ? Math.round(r) : r);
        case "<":
            return new Value(Value.number(args[0]) < Value.number(args[1]));
        case "=":
            return new Value(Util.equals(args[0], args[1]));
        case ">":
            return new Value(Value.number(args[0]) > Value.number(args[1]));
        case "&":
            return new Value(Value.bool(args[0]) && Value.bool(args[1]));
        case "|":
            return new Value(Value.bool(args[0]) || Value.bool(args[1]));
        case "not":
            return new Value(!Value.bool(args[0]));
        case "concatenate:with:":
            return new Value(Value.string(args[0]) + Value.string(args[1]));
        case "letter:of:":
            int l = (int) Value.number(args[0]) - 1;
            String s = Value.string(args[1]);
            return new Value(l < 0 || l >= s.length() ? "" : Character.toString(s.charAt(l)));
        case "stringLength:":
            return new Value(Value.string(args[0]).length());
        case "%":
            return new Value(Util.mod(Value.number(args[0]), Value.number(args[1])));
        case "rounded":
            return new Value(Math.round(Value.number(args[0])));
        case "computeFunction:of:":
            double n = Value.number(args[1]);
            switch (Value.string(args[0])) {
            case "abs":
                return new Value(Math.abs(n));
            case "floor":
                return new Value(Math.floor(n));
            case "ceiling":
                return new Value(Math.ceil(n));
            case "sqrt":
                return new Value(Math.sqrt(n));
            case "sin":
                return new Value(Math.sin(Math.toRadians(n)));
            case "cos":
                return new Value(Math.cos(Math.toRadians(n)));
            case "tan":
                return new Value(Math.tan(Math.toRadians(n)));
            case "asin":
                return new Value(Math.toDegrees(Math.asin(n)));
            case "acos":
                return new Value(Math.toDegrees(Math.acos(n)));
            case "atan":
                return new Value(Math.toDegrees(Math.atan(n)));

            case "ln":
                return new Value(Math.log(n));
            case "log":
                return new Value(Math.log10(n));
            case "10 ^":
                return new Value(Math.pow(10, n));
            case "e ^":
                return new Value(Math.pow(Math.E, n));
            }
            throw new RuntimeException("Undefined function " + args[0]);
        // OPERATORS END

        // DATA BEGIN
        case "readVariable":
            return getVariable(Value.string(args[0])).getValue();
        case "setVar:to:":
            getVariable(Value.string(args[0])).setValue(args[1]);
            return null;
        case "changeVar:by:":
            Variable var = getVariable(Value.string(args[0]));
            var.setValue(new Value(Value.number(var.getValue()) + Value.number(args[1])));
            return null;

        case "append:toList:":
            getList(Value.string(args[1])).add(args[0]);
            return null;
        case "deleteLine:ofList:":
            getList(Value.string(args[1])).delete(args[0]);
            return null;
        case "insert:at:ofList:":
            getList(Value.string(args[2])).insert(args[1], args[0]);
            return null;
        case "setLine:ofList:to:":
            getList(Value.string(args[1])).replace(args[0], args[2]);
            return null;
        case "getLine:ofList:":
            return getList(Value.string(args[1])).get(args[0]);
        case "lineCountOfList:":
            return getList(Value.string(args[0])).size();
        case "list:contains:":
            return getList(Value.string(args[0])).contains(args[1]);
        // DATA END
        default:
            System.err.println("Unknow selector: " + selector);
            return null;
            //throw new RuntimeException("Unknow selector: " + selector);
        }
    }

    public void draw(RenderTarget t) {
        for (Entry<String, Float> e : effects.entrySet()) {
            shader.setParameter(e.getKey(), e.getValue() / 100);
        }
        sprite.draw(t, renderStates);
    }

    public static final Shader shader;
    public static final RenderStates renderStates;

    static {
        shader = new Shader();
        try {
            shader.loadFromStream(Scriptable.class.getResourceAsStream("/effects.frag"), Shader.Type.FRAGMENT);
        } catch (IOException | ShaderSourceException ex) {
            Logger.getLogger(Scriptable.class.getName()).log(Level.SEVERE, null, ex);
        }
        shader.setParameter("texture", Shader.CURRENT_TEXTURE);
        renderStates = new RenderStates(shader);
    }
}
