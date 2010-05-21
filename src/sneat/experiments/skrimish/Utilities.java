package sneat.experiments.skrimish;

import java.util.Random;

class Utilities {
    public static final float twoPi = 2 * (float) Math.PI;
    public static final float piOverTwo = (float) Math.PI / 2.0f;
    public static Random r = new Random();
    public static int shiftDown = 20;
    public static int predStrike = 25;

    public static float Distance(Drawable a, Drawable b) {
        float xDist = a.x - b.x;
        float yDist = a.y - b.y;
//#if EUCLID
        return (float) Math.sqrt(xDist * xDist + yDist * yDist);
//#else
//            return Math.abs(a.x - b.x) + Math.Abs(a.y - b.y);
//#endif
    }
}
