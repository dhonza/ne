package sneat.experiments.skrimish;

import sneat.neuralnetwork.INetwork;

public abstract class Agent extends Drawable {
    public float heading;
    public float radius = 125;
    public int hitpoints = 1;

    public Agent() {
        super();
//            color = System.Drawing.Brushes.Blue;
        heading = 0;
    }

    public Agent(float x, float y, float w, float h) {
        super(x, y, w, h);
//            color = System.Drawing.Brushes.Blue;
        heading = (float) (3 * Math.PI / 2.0);
    }

    public Agent(float x, float y, float w, float h, INetwork n) {
        super(x, y, w, h);
//            color = System.Drawing.Brushes.Black;
        heading = (float) (3 * Math.PI / 2.0);
    }

    public void turn(float radians) {
        heading += radians;
        //keep the heading between 0 and 2PI
        if (heading >= Utilities.twoPi)
            heading -= Utilities.twoPi;
        if (heading < 0)
            heading += Utilities.twoPi;
    }

    public void move(float speed) {
        x += speed * (float) Math.cos(heading);
        y += speed * (float) Math.sin(heading);
    }

    public boolean isActionable(Agent a) {
        return Utilities.Distance(a, this) < radius;
    }
}
