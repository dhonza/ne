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

import hyper.experiments.octopusArm.OctopusArm;
import net.phys2d.raw.Body;
import net.phys2d.raw.SpringJoint;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

/**
 * @author Brian Woolley on 13 October 2009
 */
public class Factory {

    /**
     * TODO Add JavaDoc Comments
     *
     * @param world
     */
    protected Factory(World world) {
        assert (world != null);
        f_world = world;
    }

    /**
     * Create an Octopus Arm composed of n hydrostatic segments.
     *
     * @param n The number of segments in the arm
     * @return The reference to the ArmController
     */
    protected Arm createNewArm(int n) {
        assert (n >= OctopusArm.MIN_SEGMENTS);     // Error creating an arm with less than 1 segment
        assert (n <= OctopusArm.MAX_SEGMENTS);      // We're not ready to try more than 10 segments yet

        Arm newArm = new Arm();
        Body dorsal, ventral;

        double x = -0.9;                                // X position for segment
        double y = 0;                                    // Centerline function
        double l = OctopusArm.getArmLength() / n;            // Segment length
        double w = OctopusArm.getArmWidth() / 2;            // Segment width, +/- from centerline
        double mass = 500 / n;                            // Mass of each node
        double t = OctopusArm.getMuscleActivation();    // The muscle's minimum contractive force
        double scale = 1;                    // Scaling Factor (creates tapered effect)
        double taper = OctopusArm.getArmTaper() / n;

        // Generate two nodes for the base
        dorsal = createNewPointMass("d0", x, y + w * scale, mass * scale);
        ventral = createNewPointMass("v0", x, y - w * scale, mass * scale);
        newArm.add(createNewArmBase(dorsal, ventral));

        //and two for each arm segment
        // Set the position for the dorsal and ventral vertices
        // Create a segment and add it to the previous segment
        for (int i = 1; i < n + 1; i++) {
            x += l;
            scale -= taper;
            dorsal = createNewPointMass("d" + i, x, y + w * scale, mass * scale);
            ventral = createNewPointMass("v" + i, x, y - w * scale, mass * scale);

            Segment current = createNewArmSegment(newArm.getSegment(i - 1), dorsal, ventral, t * scale);
            newArm.add(current);

            // TODO:  Allow a strategy to set the position of the arm
        }
        return newArm;
    }

    /**
     * Create a new base segment for the Octopus Arm
     *
     * @param dorsal  The upper (dorsal) pointMass in this segment
     * @param ventral The lower (ventral) pointMass in this segment
     * @return The reference to the new ArmBase segment
     */
    protected ArmBase createNewArmBase(Body dorsal, Body ventral) {
        return new ArmBase(dorsal, ventral);
    }

    /**
     * Create a new 2D hydostatic arm segment for the Octopus Arm that is connected to a previous segment
     *
     * @param previousSegment The segment that this arm segment is connected to
     * @param dorsalNode      The upper (dorsal) point mass in this segment
     * @param ventralNode     The lower (ventral) point mass in this segment
     * @param activation      The minimum/relaxed contractive force of the muscle
     * @return The reference to the new ArmSegment segment
     */
    protected ArmSegment createNewArmSegment(Segment previousSegment,
                                             Body dorsalNode,
                                             Body ventralNode,
                                             double activation) {
        Muscle dorsalMuscle = createNewMuscle(previousSegment.getDorsalNode(), dorsalNode, activation);
        Muscle transverseMuscle = createNewMuscle(dorsalNode, ventralNode, 2 * activation);
        Muscle ventralMuscle = createNewMuscle(previousSegment.getVentralNode(), ventralNode, activation);
        return new ArmSegment(previousSegment, dorsalMuscle, transverseMuscle, ventralMuscle);
    }

    /**
     * Create a Muscle that connects body 1 to body 2 via using a linear spring joint model
     * (phys2d.raw.SpringJoint). The resistance component sets the tension of the muscle when relaxed.
     *
     * @param body1      is attached to body2 via the musculature
     * @param body2      is attached to body1 via the musculature
     * @param activation sets the operating range of the muscle contractive force
     * @return The reference to the new Muscle segment
     */
    protected Muscle createNewMuscle(Body body1, Body body2, double activation) {
        SpringJoint spring = new SpringJoint(body1, body2, body1.getPosition(), body2.getPosition());
        f_world.add(spring);
        return new Muscle(spring, activation);
    }

    /**
     * Create a new point mass located at (x, y)
     *
     * @see hyper.experiments.octopusArm.model.Factory#createNewBody()
     */
    protected Body createNewPointMass(String label, double x, double y) {
        return createNewPointMass(label, x, y, Environment.MASS);
    }

    /**
     * Create a new point with a specific mass located at (x, y)
     *
     * @see hyper.experiments.octopusArm.model.Factory#createNewBody()
     */
    protected Body createNewPointMass(String label, double x, double y, double mass) {
        return createNewBody(label, x, y, Environment.RADIUS, mass);
    }

    /**
     * Create an immovable target located at (x, y)
     *
     * @see hyper.experiments.octopusArm.model.Factory#createNewBody()
     */
    protected Body createNewTarget(String label, double x, double y, double radius) {
        Body target = createNewBody(label, x, y, radius, Environment.MASS);
        target.setMoveable(false);
        return target;
    }

    /**
     * Create a new body with a specific mass and radius located at (x, y)
     *
     * @param label  The name of the body
     * @param x      The x position of the point mass
     * @param y      The y position of the point mass
     * @param radius The radius of the target
     * @param mass   The mass of the body
     * @return The reference to the new point mass
     */
    protected Body createNewBody(String label, double x, double y, double radius, double mass) {
        Body body = new Body(label, new Circle((float) radius), (float) mass);
        body.setPosition((float) x, (float) y);
        body.setDamping(1.0f);
        body.setFriction(0.0f);
        f_world.add(body);
        return body;
    }

    private final World f_world;
}
