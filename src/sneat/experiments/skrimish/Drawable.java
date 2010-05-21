package sneat.experiments.skrimish;

import java.awt.*;

public abstract class Drawable {
    public float x;
    public float y;
    public float height;
    public float width;
//    public Brush color;

    public Drawable() {
        x = 0;
        y = 0;
        height = 0;
        width = 0;
    }

    public Drawable(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }

    public void Draw(Graphics g) {
//        g.FillRectangle(color, x - width / 2.0f, Utilities.shiftDown + (y - height / 2.0f), width, height);
    }

}