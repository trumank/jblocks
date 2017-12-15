package jblocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import com.fasterxml.jackson.databind.JsonNode;

public class Stage extends Scriptable {
    RenderTexture pen;
    org.jsfml.graphics.Sprite penSprite;
    Vector2f size;
    java.util.List<Sprite> sprites;
    Map<String, Scriptable> objects;
    Vector2f mouse;
    boolean mouseDown;
    Timer timer;
    Set<String> keys;

    public Stage(JsonNode config) {
        super(config);
        size = new Vector2f(480, 360);
        pen = new RenderTexture();
        try {
            pen.create((int) size.x, (int) size.y);
        } catch (TextureCreationException e) {
            System.out.println("Error creating pen texture: " + e.getMessage());
        }
        pen.clear(Color.TRANSPARENT);
        pen.setView(getView());
        penSprite = new org.jsfml.graphics.Sprite(pen.getTexture());
        penSprite.setPosition(size.x / -2, size.y / 2);
        penSprite.setScale(1, -1);
        objects = new HashMap<>();
        objects.put(getName(), this);
        sprites = new ArrayList<>();
        for (JsonNode child : config.get("children")) {
            if (child.get("objName") != null) {
                Sprite sprite = new Sprite(child, this);
                sprites.add(sprite);
                objects.put(sprite.getName(), sprite);
            }
        }
        mouse = Vector2f.ZERO;
        timer = new Timer();
        keys = new HashSet<>();
        updateSprite();
    }

    public void start() {
        globalFireEvent("whenGreenFlag");
    }

    @Override
    public void tick() {
        super.tick();
        for (Sprite sp : sprites) {
            sp.tick();
        }
    }

    @Override
    public Stage getStage() {
        return this;
    }

    public Scriptable getObject(String name) {
        return objects.get(name);
    }

    public void globalFireEvent(String event) {
        fireEvent(event);
        for (Sprite sp : sprites) {
            sp.fireEvent(event);
        }
    }

    public boolean globalIsEventRunning(String event) {
        for (Sprite sprite : sprites) {
            if (sprite.isEventRunning(event)) {
                return true;
            }
        }
        return false;
    }

    public Vector2f getSize() {
        return size;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public View getView() {
        return new View(new Vector2f(0, 0), size);
    }

    public void setMouseDown(boolean down) {
        mouseDown = down;
    }

    public void setMousePostition(Vector2f p) {
        mouse = p;
    }

    public void keyPressed(Key key) {
        String name = Util.keyName(key);
        globalFireEvent("whenKeyPressed" + name);
        keys.add(name);
    }

    public void keyReleased(Key key) {
        keys.remove(Util.keyName(key));
    }

    public boolean isKeyPressed(String name) {
        return keys.contains(name);
    }

    public boolean getMouseDown() {
        return mouseDown;
    }

    public double getMouseX() {
        return mouse.x - size.x / 2;
    }

    public double getMouseY() {
        return size.y / 2 - mouse.y;
    }

    public RenderTarget getCanvas() {
        return pen;
    }

    public Timer getTimer() {
        return timer;
    }

    @Override
    public Value getAttribute(String attribute) {
        switch (attribute) {
        case "backdrop #":
            return new Value(costumeIndex + 1);
        case "backdrop name":
            return new Value(getCostume().getName());
        default:
            return super.getAttribute(attribute);
        }
    }

    @Override
    public void draw(RenderTarget t) {
        super.draw(t);
        t.draw(penSprite);
        for (Sprite sprite : sprites) {
            sprite.draw(t);
        }
    }
}
