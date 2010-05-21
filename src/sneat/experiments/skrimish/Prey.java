package sneat.experiments.skrimish;

import sneat.neuralnetwork.INetwork;

public class Prey extends Agent {
    public Predator closestPred = null;
    public float closestPredDist = Float.MAX_VALUE;

    public Prey() {
        super();
        radius = 50;
    }

    public Prey(float x, float y, float w, float h) {
        super(x, y, w, h);
        radius = 50;
    }

    public Prey(float x, float y, float w, float h, INetwork network) {
        super(x, y, w, h);
        radius = 50;
    }

    //face the opposite direction and flee if the predator is closer than the radius
    public void determineAction() {
        if (closestPredDist <= radius) {
            setHeading();
            move(World.step);
        }
        closestPred = null;
        closestPredDist = Float.MAX_VALUE;
    }

    //forces the prey to face opposite the predator
    private void setHeading() {
        float xDist = closestPred.x - x;
        float yDist = closestPred.y - y;

        float desiredAngle = (float) Math.atan2(yDist, xDist);
        desiredAngle += (float) Math.PI;
        if (desiredAngle < 0)
            desiredAngle += Utilities.twoPi;
        if (desiredAngle > Utilities.twoPi)
            desiredAngle -= Utilities.twoPi;
        heading = desiredAngle;
    }
}