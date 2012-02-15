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

import hyper.experiments.octopusArm.Util;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Woolley on 13 October 2009
 *         TODO Add JavaDoc Comments
 */
public class ArmBase implements Segment {

    /**
     * Constructs a new arm base where the dorsal and ventral nodes provided are the fixed base of the octopus arm.
     *
     * @param dorsalNode  The upper (dorsal) node of the arm base.
     * @param ventralNode The lower (ventral) node of the arm base.
     */
    public ArmBase(Body dorsalNode, Body ventralNode) {
        assert (dorsalNode != null);
        assert (ventralNode != null);

        f_dNode = dorsalNode;
        f_vNode = ventralNode;

        // This is a fixed base segment until the rotation is implemented
        f_dNode.setMoveable(false);
        f_vNode.setMoveable(false);
    }

    /**
     * Advances the arm base one simulation timestep.  While the arm base is immovable, this operation currently has no effect.
     *
     * @see hyper.experiments.octopusArm.model.Simulatable#step()
     */
    public void step() {
    }

    /**
     * Returns the amount of energy expended by this segment in this timestep.  While the arm base is immovable, this returns 0.0.
     *
     * @return The amount of energy expended in this timestep.
     * @see Simulatable#getEnergyUsed()
     */
    public double getEnergyUsed() {
        return 0;
    }

    /**
     * Returns the upper (dorsal) node of this segment.
     *
     * @return The dorsal node of this segment.
     * @see Segment#getDorsalNode()
     */
    public Body getDorsalNode() {
        return f_dNode;
    }

    /**
     * Returns the lower (ventral) noded of this segment.
     *
     * @return The ventral node of this segment.
     * @see Segment#getVentralNode()
     */
    public Body getVentralNode() {
        return f_vNode;
    }

    /**
     * Calculates the midpoint between the dorsal and ventral nodes (alpha) at this timestep.
     *
     * @return The midpoint between the dorsal and ventral nodes.
     * @see Segment#getAlpha()
     */
    public ROVector2f getAlpha() {
        // Calculate the point (alpha) between the dorsal and ventral nodes
        float x = f_vNode.getPosition().getX() + (f_dNode.getPosition().getX() - f_vNode.getPosition().getX()) / 2;
        float y = f_vNode.getPosition().getY() + (f_dNode.getPosition().getY() - f_vNode.getPosition().getY()) / 2;
        return new Vector2f(x, y);
    }

    /**
     * Calculates the orientation of this segment.  The alpha angle is the angle perpendicular to the the transverse link
     * at this timestep.
     *
     * @return The current orientation of this segment.
     * @see Segment#getAlphaAngle()
     */
    public double getAlphaAngle() {
        return Util.addAngles(Util.angleOf(f_vNode.getPosition(), f_dNode.getPosition()), -Math.PI / 2);
    }

    /**
     * There is no previous segment for the base, thus it returns a reference to itself
     * to avoid returning NULL.
     *
     * @return A reference to itself is used to avoid returning NULL.
     * @see hyper.experiments.octopusArm.model.Segment#getPreviousSegment()
     */
    public Segment getPreviousSegment() {
        return this;
    }

    /**
     * Applies a rotational force at the arm's base.  While the arm base is immovable, this method has no effect.
     *
     * @param force where positive values indicate a counter-clockwise rotation
     */
    public void setRotationalForce(double force) {
        if (force > MAX_FORCE) force = MAX_FORCE;
        if (force < MIN_FORCE) force = MIN_FORCE;
    }

    /**
     * Returns a collection of all the {@link Body} objects in this segment.
     *
     * @return The collection of {@link Body} objects in this segment.
     * @see Simulatable#getAllBodies()
     */
    public Collection<Body> getAllBodies() {
        Collection<Body> bodies = new ArrayList<Body>();
        bodies.add(f_dNode);
        bodies.add(f_vNode);
        return bodies;
    }

    private Body f_dNode;    // my dorsal node
    private Body f_vNode;    // my ventral node

    private static final double MAX_FORCE = 1.0;
    private static final double MIN_FORCE = 0.0;
}
