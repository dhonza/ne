/**
 * Copyright (C) 2011 Brian Woolley
 *
 * This file is part of the octopusArm simulator.
 *
 * The octopusArm simulator is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *
 * created by Brian Woolley on May 6, 2010
 */
package hyper.experiments.octopusArm.model;

import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

/**
 * Targets are circular bodies within the simulation environment that can be seen and touched by the octopus arm.
 * The current implementation creates immovable targets at a given (x,y) position, i.e. they are not affected by gravity, buoyancy, or impact forces.
 *
 * @author Brian Woolley
 */
public class Target {

    /**
     * Creates a new {@link Target} located at (x,y) with the radius specified.
     *
     * @param x      The x position of the target.
     * @param y      The y position of the target.
     * @param radius The radius of the circular target.
     */
    public Target(double x, double y, double radius) {
        myBody = new Body(new Circle((float) radius), Environment.MASS);
        myBody.setPosition((float) x, (float) y);
        myBody.setMoveable(false);
    }

    /**
     * Returns the current x position of the target.
     *
     * @return The target's x position.
     */
    public double getX() {
        return myBody.getPosition().getX();
    }

    /**
     * Returns the current y position of the target.
     *
     * @return The target's y position.
     */
    public double getY() {
        return myBody.getPosition().getY();
    }

    /**
     * Returns the width (diameter) of the target.
     *
     * @return The target width.
     */
    public double getWidth() {
        return myBody.getShape().getBounds().getWidth();
    }

    /**
     * Returns the height (diameter) of the target.
     *
     * @return The target height.
     */
    public double getHeight() {
        return myBody.getShape().getBounds().getHeight();
    }

    /**
     * Returns the radius of the target.
     *
     * @return The target radius.
     */
    public double getRadius() {
        return getWidth() / 2;
    }

    /**
     * Returns the physics body of the target.
     *
     * @return The physics body of the target.
     */
    public Body getBody() {
        return myBody;
    }

    /**
     * Returns the position (x,y) of the target center.
     *
     * @return The position of the target center.
     */
    public ROVector2f getPosition() {
        return myBody.getPosition();
    }

    private final Body myBody;

}
