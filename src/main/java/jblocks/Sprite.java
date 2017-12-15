package jblocks;

import org.jsfml.graphics.RenderTarget;

import com.fasterxml.jackson.databind.JsonNode;

public class Sprite extends Scriptable {
    Point position;
    double rotation;
    double scale;
    boolean visible;
    Pen pen;
    Stage stage;

    public Sprite(JsonNode config, Stage stage) {
        super(config);
        this.stage = stage;
        position = new Point(config.path("scratchX").doubleValue(), config.path("scratchY").doubleValue());
        setRotation(config.path("direction").floatValue());
        visible = config.path("visible").booleanValue();
        setScale(config.path("scale").floatValue());
        pen = new Pen(getStage().getCanvas());
        updateSprite();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public Variable getVariable(String name) {
        Variable v = variables.get(name);
        if (v != null) {
            return v;
        }
        return getStage().getVariable(name);
    }

    @Override
    public List getList(String name) {
        List l = lists.get(name);
        if (l != null) {
            return l;
        }
        return getStage().getList(name);
    }

    public void setPosition(double x, double y) {
        pen.drawLine((float) position.x, (float) position.y, (float) x, (float) y);
        position.setPosition(x, y);
    }

    public void move(double dx, double dy) {
        setPosition(position.x + dx, position.y + dy);
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        sprite.setRotation((float) (rotation - 90));
    }

    public void setScale(double scale) {
        this.scale = scale;
        float f = (float) (scale / getCostume().getResolution());
        sprite.setScale(f, f);
    }

    @Override
    public void updateSprite() {
        sprite = new org.jsfml.graphics.Sprite();
        super.updateSprite();
        setScale(scale);
        sprite.setRotation((float) (rotation - 90));
        float f = (float) (scale / getCostume().getResolution());
        sprite.setScale(f, f);
        sprite.setPosition((float) position.x, (float) -position.y);
    }

    @Override
    public Value getAttribute(String attribute) {
        switch (attribute) {
        case "x position":
            return new Value(position.x);
        case "y position":
            return new Value(position.y);
        case "direction":
            return new Value(rotation);
        case "costume #":
            return new Value(costumeIndex + 1);
        case "costume name":
            return new Value(getCostume().getName());
        case "size":
            return new Value(scale * 100);
        default:
            return super.getAttribute(attribute);
        }
    }

    @Override
    public Value eval(String selector, Value[] args) {
        switch (selector) {
        // MOTION BEGIN
        case "forward:":
            double steps = Value.number(args[0]);
            double rad = Math.toRadians(rotation - 90);
            move(Math.cos(rad) * steps, Math.sin(rad) * steps);
            return null;
        case "turnRight:":
            setRotation(rotation + (float) Value.number(args[0]));
            return null;
        case "turnLeft:":
            setRotation(rotation - (float) Value.number(args[0]));
            return null;
        case "heading:":
            setRotation((float) Value.number(args[0]));
            return null;
        case "gotoX:y:":
            setPosition(Value.number(args[0]), Value.number(args[1]));
            return null;
        case "changeXposBy:":
            setPosition(position.x + Value.number(args[0]), position.y);
            return null;
        case "xpos:":
            setPosition(Value.number(args[0]), position.y);
            return null;
        case "changeYposBy:":
            setPosition(position.x, position.y + Value.number(args[0]));
            return null;
        case "ypos:":
            setPosition(position.y, Value.number(args[0]));
            return null;
        case "xpos":
            return new Value(position.x);
        case "ypos":
            return new Value(position.y);
        case "heading":
            return new Value(rotation);
        // MOTION END

        // LOOKS BEGIN
        case "say:":
            System.out.println(args[0]);
            return null;
        case "show":
            visible = true;
            return null;
        case "hide":
            visible = false;
            return null;
        case "setSizeTo:":
            setScale(Value.number(args[0]) / 100);
            return null;
        case "scale":
            return new Value(scale * 100);
        // LOOKS END

        // PEN BEGIN
        case "stampCostume":
            boolean v = visible;
            visible = true;
            draw(getStage().getCanvas());
            visible = v;
            return null;
        case "putPenDown":
            pen.setDown();
            pen.drawLine((float) position.x, (float) position.y, (float) position.x, (float) position.y);
            return null;
        case "putPenUp":
            pen.setUp();
            return null;
        case "penColor:":
            int n = (int) Value.number(args[0]);
            pen.setColor(n >> 16 & 255, n >> 8 & 255, n & 255);
            return null;
        case "changePenHueBy:":
            pen.setHue((float) Util.mod(pen.getHue() + Value.number(args[0]), 200));
            return null;
        case "setPenHueTo:":
            pen.setHue((float) Util.mod(Value.number(args[0]), 200));
            return null;
        case "changePenShadeBy:":
            float l1 = (float) Util.mod(pen.getLightness() + Value.number(args[0]), 200) / 100;
            pen.setLightness(l1 > 1 ? 1 - l1 : l1);
            return null;
        case "setPenShadeTo:":
            float l2 = (float) Util.mod(Value.number(args[0]), 200) / 100;
            pen.setLightness(l2 > 1 ? 1 - l2 : l2);
            return null;
        case "changePenSizeBy:":
            pen.setSize((float) (pen.getSize() + Value.number(args[0])));
            return null;
        case "penSize:":
            pen.setSize((float) Value.number(args[0]));
            return null;
        // PEN END
        default:
            return super.eval(selector, args);
        }
    }

    @Override
    public void draw(RenderTarget t) {
        if (visible) {
            sprite.setPosition((float) position.x, (float) -position.y);
            super.draw(t);
        }
    }
}
