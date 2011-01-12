/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution
 * written as a bachelor project
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */
package hyper.experiments.robots.distance;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import robot.RobotWithSensors;
import vivae.arena.Arena;
import vivae.arena.parts.Robot;
import vivae.arena.parts.VivaeObject;
import vivae.arena.parts.sensors.DistanceSensor;
import vivae.arena.parts.sensors.Sensor;
import vivae.util.Util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author HKou
 */
public class NetControlledRobotDistance extends RobotWithSensors {

    protected double[] sensoryData;

    public NetControlledRobotDistance(float x, float y) {
        super(x, y);
        sensors = new Vector<Sensor>();
        sensorsMap = new HashMap<Integer, Sensor>();
    }

    public NetControlledRobotDistance(Shape shape, int layer, Arena arena) {
        this((float) shape.getBounds2D().getCenterX(),
                (float) shape.getBounds2D().getCenterY(), arena);
    }

    public NetControlledRobotDistance(float x, float y, Arena arena) {
        this(x, y);
        diameter = 12;
        boundingCircleRadius = (float) Math.sqrt(2 * diameter * diameter) / 2;
        myNumber = getNumber();
        this.arena = arena;
        this.world = arena.getWorld();
        body = new Body("Robot", new Box(diameter, diameter), 50f);
        body.setPosition(x, y);
        body.setRotation(0);
        body.setDamping(baseDamping);
        body.setRotDamping(ROT_DAMPING_MUTIPLYING_CONST * baseDamping);
        setShape(new Rectangle2D.Double(0, 0, diameter, diameter));
        Rectangle r = getShape().getBounds();
        centerX = (float) r.getCenterX();
        centerY = (float) r.getCenterY();
    }

    public void setSensors(int howMany, double startingAngle, double angleIncrement, double maxDistance) {
        for (int i = 0; i < howMany; i++) {
            addDistanceSensor(startingAngle + i * angleIncrement, maxDistance);
        }
    }

    public void addDistanceSensor(Double angle, double maxDistance) {
        Sensor s = new DistanceSensor(this, angle, sensorNumber, maxDistance);
        sensors.add(s);
        sensorsMap.put(sensorNumber, s);
        sensorNumber++;
    }

    public double[] getSensoryData() {
        double[] data = new double[sensorNumber];
        int di = 0, si = 0;
        double v;
        Vector<VivaeObject> allObjects = getArena().getVivaes();
        for (Sensor sensor : sensors) {
            if (sensor instanceof DistanceSensor) {
                v = ((DistanceSensor) sensor).getDistance(allObjects);
                data[si] = v;
                si++;
            }
        }
        sensoryData = data;
        return data;
    }

    @Override
    public void moveComponent() {
        inMotion = true;
        direction = body.getRotation();
        net.phys2d.math.ROVector2f p = body.getPosition();
        x = p.getX();
        y = p.getY();
        for (Sensor s : sensors) {
            s.moveComponent();
        }

        final double distance = Util.euclideanDistance(lastX, lastY, x, y);
        final double velDist = lastVelocity - getSpeed() > 0 ? lastVelocity - getSpeed() : 0;
        if (velDist > 10) {
            crashmeter += velDist;
        }
        if (velDist > maxDeceleration) {
            maxDeceleration = velDist;
        }
        overallDeceleration += velDist;
        lastVelocity = getSpeed();

        odometer += distance;
        lastX = x;
        lastY = y;


    }

    @Override
    public AffineTransform getTranslation() {
        AffineTransform af = AffineTransform.getTranslateInstance(x - diameter / 2, y - diameter / 2);
        af.rotate(direction, centerX, centerY);
        return af;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Object hint = new Object();
        if (isAntialiased()) {
            hint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        translation = getTranslation();
        Color oldColor = g2.getColor();
        g2.setColor(new Color(230, 230, 250));
        g2.fill(translation.createTransformedShape(getShape()));
        g2.setColor(Color.BLACK);
        g2.draw(translation.createTransformedShape(getShape()));
        if (isShowingSensors) {
            for (Sensor s : sensors) {
                s.paintComponent(g2);
            }
        }
        if (isShowingStatusFrame) {
            paintStatusFrame(g2);
        }
        g2.setColor(oldColor);
        if (isAntialiased()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hint);
        }
    }

    @Override
    public void accelerate(float s) {
        setSpeed(body.getVelocity().length());
        s = Math.min(s, getMaxSpeed() - (float) getSpeed());
        float dx = s * (float) Math.cos(body.getRotation() - Math.PI / 2);
        float dy = s * (float) Math.sin(body.getRotation() - Math.PI / 2);
        body.adjustVelocity(new Vector2f(dx, dy));

    }

    @Override
    public void decelerate(float s) {
        setSpeed(body.getVelocity().length());
        s = Math.max(s, 0);
        float dx = s * (float) Math.cos(body.getRotation() - Math.PI / 2);
        float dy = s * (float) Math.sin(body.getRotation() - Math.PI / 2);
        body.adjustVelocity(new Vector2f(-dx, -dy));
    }

    @Override
    public void rotate(float radius) {
        body.adjustAngularVelocity(radius);
        this.direction = body.getRotation();
    }

    @Override
    public int getNumber() {
        return robotsCounter++;
    }

    @Override
    public String getActiveName() {
        return "Robot";
    }

    @Override
    public float getAcceleration() {
        return Robot.ACCELERATION;
    }

    @Override
    public float getMaxSpeed() {
        return Robot.MAX_SPEED;
    }

    @Override
    public float getRotationIncrement() {
        return Robot.ROTATION;
    }

    @Override
    public String toString() {
        return "Robot " + myNumber;
    }

    @Override
    public Vector<Sensor> getSensors() {
        return sensors;
    }

    @Override
    public void reportObjectOnSight(Sensor s, Body b) {
        System.out.println("Object seen from sensor " + s);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void setShowingSensors(boolean showingSensors) {
        isShowingSensors = showingSensors;
    }

    @Override
    public void paintStatusFrame(Graphics g, int baseX, int baseY) {
        Graphics2D g2 = (Graphics2D) g;
        Color oldColor = g2.getColor();
        Composite oldComposite = g2.getComposite();
        g2.setComposite(opacityBack);
        g2.setColor(Color.BLACK);
        g2.fillRect(baseX, baseY, 100, 100);
        g2.setComposite(opacityFront);
        g2.setColor(Color.WHITE);
        g2.drawRect(baseX, baseY, 100, 100);
        if (isStatusFramePinedToPosition) {
            g2.drawLine((int) this.x, (int) this.y, baseX + 100, baseY + 100);
        }
        baseX += 5;
        baseY += 15;
        g2.drawString(String.format(getActiveName() + "  #%d", myNumber), baseX, baseY);
        baseY += STATUS_FRAME_LINE_HEIGHT;
        g2.drawString(String.format("x: %4.0f", x), baseX, baseY);
        baseY += STATUS_FRAME_LINE_HEIGHT;
        g2.drawString(String.format("y: %4.0f", y), baseX, baseY);
        baseY += STATUS_FRAME_LINE_HEIGHT;
        String s = "";
        for (int j = 0; j < sensoryData.length; j++) {
            s += String.format("%1.1f ", sensoryData[j]);
        }
        g2.drawString(s, baseX, baseY);
        g2.setComposite(oldComposite);
        g2.setColor(oldColor);
    }
}

