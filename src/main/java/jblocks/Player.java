package jblocks;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

public class Player implements Runnable {
    RenderWindow window;
    Stage stage;

    public Player(Stage stage) {
        this.stage = stage;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void run() {
        window = new RenderWindow(new VideoMode((int) stage.getWidth(), (int) stage.getHeight()), "JBlocks");
        window.setView(stage.getView());

        boolean turbo = false;
        stage.start();

        Timer timer = new Timer();
        while (window.isOpen()) {
            Event event;
            while ((event = window.pollEvent()) != null) {
                switch (event.type) {
                case CLOSED:
                    window.close();
                    break;
                case KEY_PRESSED:
                    stage.keyPressed(event.asKeyEvent().key);
                    break;
                case KEY_RELEASED:
                    stage.keyReleased(event.asKeyEvent().key);
                    break;
                case MOUSE_BUTTON_PRESSED:
                    stage.setMouseDown(true);
                    break;
                case MOUSE_BUTTON_RELEASED:
                    stage.setMouseDown(false);
                    break;
                case MOUSE_MOVED:
                    Vector2i p = event.asMouseEvent().position;
                    Vector2f ss = stage.getSize();
                    Vector2i ws = window.getSize();
                    stage.setMousePostition(new Vector2f(ss.x / ws.x * p.x, ss.y / ws.y * p.y));
                    break;
                }
            }
            timer.start(16);
            do {
                stage.tick();
            } while (turbo && !timer.ended());
            window.clear(Color.WHITE);
            viewStage();
            window.display();
        }
    }

    public void viewStage() {
        stage.draw(window);
    }
}
