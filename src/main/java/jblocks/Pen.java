package jblocks;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public class Pen {
    RenderTarget canvas;
    RectangleShape line;
    boolean down;
    float size;
    Color color;
    float h, s, l;

    public Pen(RenderTarget c) {
        canvas = c;
        line = new RectangleShape();
        down = false;
        size = 1;
        h = 2/3f;
        s = 1.0f;
        l = 0.5f;
        updateColor();
    }

    public void setDown() {
        down = true;
    }

    public void setUp() {
        down = false;
    }

    public void setSize(float s) {
        size = s;
    }

    public float getSize() {
        return size;
    }

    public void setColor(int r, int g, int b) {
        color = new Color(r, g, b);
    }

    public void setHue(float h) {
        this.h = h;
        updateColor();
    }

    public void setSaturation(float s) {
        this.s = s;
        updateColor();
    }

    public void setLightness(float l) {
        this.l = l;
        updateColor();
    }

    public float getHue() {
        return h;
    }

    public float getSaturation() {
        return s;
    }

    public float getLightness() {
        return l;
    }

    public void drawLine(float x1, float y1, float x2, float y2) {
        if (!down) {
            return;
        }
        double dx = x2 - x1;
        double dy = y2 - y1;
        float d = (float) Math.sqrt(dx*dx + dy*dy);
        Vector2f s = new Vector2f(d + size, size);
        if (!line.getSize().equals(s)) {
            line.setSize(s);
        }
        line.setFillColor(color);
        line.setOrigin(size / 2, size / 2);
        line.setRotation((float) Math.toDegrees(Math.atan2(x2 - x1, y2 - y1)) - 90);
        line.setPosition(x1, -y1);
        canvas.draw(line);
    }

    float hue2rgb(float p, float q, float t) {
        if(t < 0) t += 1;
        if(t > 1) t -= 1;
        if(t < 1f/6) return p + (q - p) * 6 * t;
        if(t < 1f/2) return q;
        if(t < 2f/3) return p + (q - p) * (2f/3 - t) * 6;
        return p;
    }

    void updateColor() {
        int r, g, b;

        if(s == 0) {
            r = g = b = (int) (l * 255);
        } else {
            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = (int) (hue2rgb(p, q, h + 1f/3) * 255);
            g = (int) (hue2rgb(p, q, h) * 255);
            b = (int) (hue2rgb(p, q, h - 1f/3) * 255);
        }

        color = new Color(r, g, b);
    }
}
