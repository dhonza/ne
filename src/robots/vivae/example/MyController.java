/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package robots.vivae.example;

import robots.vivae.controllers.*;
import java.util.Iterator;
import java.util.Vector;

import robots.vivae.arena.parts.Movable;
import robots.vivae.arena.parts.Surface;
import robots.vivae.arena.parts.sensors.Sensor;
import robots.vivae.arena.parts.sensors.LineSensor;
import robots.vivae.arena.parts.sensors.DistanceSensor;
import robots.vivae.arena.parts.sensors.SurfaceFrictionSensor;

/**
 * A sample Controller extending RobotWithSensorController. 
 * It's logic is very simple. If it spots a fixed obstacle it turn the opposite way.
 * It's up to you to create more advanced controllers!!
 * @author Petr Smejkal
 */
public class MyController extends RobotWithSensorController{

    @Override
    public void moveControlledObject() {
       allObjects = robot.getArena().getVivaes();
       Vector<Surface> surfaces = robot.getArena().getSurfaces();
       float angle = 0f; 
        for (Iterator<Sensor> it = sensors.iterator(); it.hasNext();) {
            Sensor sensor = it.next();
            if(sensor instanceof LineSensor) {
                objectsOnSight = sensor.getVivaesOnSight(allObjects);
                if(objectsOnSight != null) {
                    if(!objectsOnSight.isEmpty()) {
                        angle = sensor.getAngle();
                        if(angle > 0 && !(objectsOnSight.get(0) instanceof Movable)) robot.rotate(-robot.getRotationIncrement());
                        else if(angle <= 0 && !(objectsOnSight.get(0) instanceof Movable)) {
                            robot.rotate(robot.getRotationIncrement());
                        }
                        break;
                    }
                }
            }
            if(sensor instanceof DistanceSensor)System.out.println(((DistanceSensor)sensor).getDistance(allObjects));
            if(sensor instanceof SurfaceFrictionSensor)System.out.println(((SurfaceFrictionSensor)sensor).getSurfaceFriction());
        }
        controlledObject.accelerate(controlledObject.getAcceleration());
    }

}
