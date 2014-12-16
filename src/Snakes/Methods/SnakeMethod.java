package Snakes.Methods;

import Snakes.Snake;
import processing.core.PApplet;

public abstract class SnakeMethod {
    Snake snake;
    public abstract void runMethod(PApplet applet);
    public void runThread(final PApplet applet) {
        snake.running = true;
        snake.inprogress = new Thread() {
            public void run() {
                runMethod(applet);
                snake.running = false;
            }
        };
        snake.inprogress.start();
    }
}
