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
import hyper.experiments.octopusArm.Util;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simulates a 2D hydrostatic structure to build a multi-segment OctopusArm
 * <p/>
 * Simulates an octopus arm segment as a 2D hydrostatic structure represented by a quadrilateral
 * polygon with fixed area.  The structure consists of three muscle segments that can be contracted
 * to manipulate the shape of the structure.  Because these structures connect to form an octopus
 * arm, transverse muscle(a) is transverse(b) from the previous arm segment.  The arm is connected
 * to a rigid base structure which gives tranverse(a) of the first segment a fixed length.
 * <p/>
 * dorsal muscle(b)
 * dNode(a) ==================== dNode(b)
 * ||                  ||
 * ||                    ||
 * ||                      ||
 * transverse  ||                        ||  transverse
 * muscle(a) ||                          ||  muscle(b)
 * ||                            ||
 * ||                              ||
 * ||                                ||
 * vNode(a) ==================================== vNode(b)
 * ventral muscle(b)
 * <p/>
 * Example:  Due to the fixed area constraint of this structure, contracting the dorsal muscle causes
 * the ventral and transverse muscles to elongate.
 *
 * @author Brian Woolley
 */
public class ArmSegment implements Segment {

    /**
     * Creates a new {@link ArmSegment} and associates it with the previous arm {@link Segment}.
     * In the case of the first {@link Segment}, the {@link ArmBase} is used as the previous segment.
     *
     * @param previousSegment The i-1 segment composing the arm.
     * @param dorsal          The upper (dorsal) muscle in the structure.
     * @param transverse      The vertical (transverse) muscle in the structure.
     * @param ventral         The lower (ventral) muscle in the structure.
     */
    public ArmSegment(Segment previousSegment, Muscle dorsal, Muscle transverse, Muscle ventral) {

        assert (previousSegment != null);
        assert (dorsal != null);
        assert (transverse != null);
        assert (ventral != null);

        previous = previousSegment;
        dorsalNodeA = previousSegment.getDorsalNode();
        ventralNodeA = previousSegment.getVentralNode();

        dorsalNodeB = dorsal.getBody2();
        ventralNodeB = ventral.getBody2();

        dorsalMuscle = dorsal;
        transverseMuscle = transverse;
        ventralMuscle = ventral;

        assert (isConvex());

        pressure = 0.0;
        area = getArea();
    }

    /**
     * Sets the muscle contraction force to a percentage of the contraction range.
     *
     * @param force The percentage of the muscle's response range.
     * @see hyper.experiments.octopusArm.model.Muscle#setContraction(double)
     */
    public void setDorsalContraction(double force) {
        dorsalMuscle.setContraction(force);
    }

    /**
     * Sets the muscle contraction force to a percentage of the contraction range.
     *
     * @param force The percentage of the muscle's response range.
     * @see hyper.experiments.octopusArm.model.Muscle#setContraction(double)
     */
    public void setTransverseContraction(double force) {
        transverseMuscle.setContraction(force);
    }

    /**
     * Sets the muscle contraction force to a percentage of the contraction range.
     *
     * @param force The percentage of the muscle's response range.
     * @see hyper.experiments.octopusArm.model.Muscle#setContraction(double)
     */
    public void setVentralContraction(double force) {
        ventralMuscle.setContraction(force);
    }

    /**
     * Advances the {@link ArmSegment} one simulation timestep.  This method ensures that the segment is convex,
     * steps the muscles, adjusts the internal pressure, and applies the buoyancy force.
     *
     * @throws InvalidArmConfigurationException
     *          if the segment fails the convex check.
     * @see hyper.experiments.octopusArm.model.Simulatable#step()
     */
    public void step() throws InvalidArmConfigurationException {

        if (!isConvex())
            throw new InvalidArmConfigurationException("Segment failed convex check...");
        stepMuscleResponse();
        adjustInternalPressure();
        applyBuoyancyForce();
    }

    /**
     * Return the previous {@link Segment} associated with this {@link Segment}.
     *
     * @return The previous {@link Segment} (i.e. closer to the {@link ArmBase}).
     * @see hyper.experiments.octopusArm.model.Segment#getPreviousSegment()
     */
    public Segment getPreviousSegment() {
        return previous;
    }

    /**
     * Return the upper (dorsal) node for the segment.
     *
     * @return The segment's dorsal node.
     * @see hyper.experiments.octopusArm.model.Segment#getDorsalNode()
     */
    public Body getDorsalNode() {
        return dorsalNodeB;
    }

    /**
     * Return the lower (ventral) node for the segment.
     *
     * @return The segment's ventral node.
     * @see hyper.experiments.octopusArm.model.Segment#getVentralNode()
     */
    public Body getVentralNode() {
        return ventralNodeB;
    }

    /**
     * Calculates the midpoint between the dorsal and ventral nodes (alpha) at this timestep.
     *
     * @return The midpoint between the dorsal and ventral nodes.
     * @see Segment#getAlpha()
     */
    public ROVector2f getAlpha() {
        // Calculate the point (alpha) between the dorsal and ventral nodes
        float x = ventralNodeB.getPosition().getX() + (dorsalNodeB.getPosition().getX() - ventralNodeB.getPosition().getX()) / 2;
        float y = ventralNodeB.getPosition().getY() + (dorsalNodeB.getPosition().getY() - ventralNodeB.getPosition().getY()) / 2;
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
        return Util.addAngles(Util.angleOf(ventralNodeB.getPosition(), dorsalNodeB.getPosition()), -Math.PI / 2);
    }

    /**
     * Computes the area of the quadralateral polygon as described by Paul Borke.
     * See: http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
     *
     * @return The current area of the segment
     */
    public double getArea() {
        double area = 0;
        ROVector2f v1, v2;

        v1 = dorsalNodeA.getPosition();
        v2 = ventralNodeA.getPosition();
        area += v1.getX() * v2.getY();
        area -= v2.getX() * v1.getY();

        v1 = ventralNodeA.getPosition();
        v2 = ventralNodeB.getPosition();
        area += v1.getX() * v2.getY();
        area -= v2.getX() * v1.getY();

        v1 = ventralNodeB.getPosition();
        v2 = dorsalNodeB.getPosition();
        area += v1.getX() * v2.getY();
        area -= v2.getX() * v1.getY();

        v1 = dorsalNodeB.getPosition();
        v2 = dorsalNodeA.getPosition();
        area += v1.getX() * v2.getY();
        area -= v2.getX() * v1.getY();

        return Math.abs(area / 2);
    }

    /**
     * Computes the mass of this quadralateral polygon using all four vertices.
     *
     * @return returns The mass of this segment.
     */
    public double getMass() {
        return dorsalNodeA.getMass() + dorsalNodeB.getMass() + ventralNodeB.getMass() + ventralNodeA.getMass();
    }

    /**
     * Returns the force being applied at this arm segment for this timestep .
     *
     * @return The forces begin applied.
     */
    public double getEnergyUsed() {
        return dorsalMuscle.getEnergyUsed() +
                transverseMuscle.getEnergyUsed() +
                ventralMuscle.getEnergyUsed();
    }

    /**
     * Return the current dorsal muscle force as a percentage of the muscle's contractive operating range.
     *
     * @return The current contraction force.
     */
    public double getDorsalContraction() {
        return dorsalMuscle.getEnergyUsed();
    }

    /**
     * Return the current transverse muscle force as a percentage of the muscle's contractive operating range.
     *
     * @return The current contraction force.
     */
    public double getTransverseContraction() {
        return transverseMuscle.getEnergyUsed();
    }

    /**
     * Return the current ventral muscle force as a percentage of the muscle's contractive operating range.
     *
     * @return The current contraction force.
     */
    public double getVentralContraction() {
        return ventralMuscle.getEnergyUsed();
    }

    /**
     * This method advances the components (i.e. the {@link Muscle}s) in this {@link ArmSegment}.
     */
    private void stepMuscleResponse() {
        dorsalMuscle.step();
        transverseMuscle.step();
        ventralMuscle.step();

        dorsalMuscle.preStep(10);
        transverseMuscle.preStep(10);
        ventralMuscle.preStep(10);
    }

    /**
     * This method adjusts the internal pressure of this {@link ArmSegment} in an attempt to maintain a constant area.
     */
    private void adjustInternalPressure() {
        double deltaArea = (area - getArea()) / area;
        pressure += deltaArea * 7.5;

        // generate pressure vectors w/ no net effect
        Vector2f c = computeImpulseVector(pressure, dorsalNodeA.getPosition(), ventralNodeB.getPosition()),
                d = computeImpulseVector(pressure, ventralNodeA.getPosition(), dorsalNodeB.getPosition());

        // apply corrective forces at each vertex
        dorsalNodeA.addForce(c.negate());
        ventralNodeA.addForce(d.negate());
        ventralNodeB.addForce(c);
        dorsalNodeB.addForce(d);
    }

    /**
     * This method applies the buoyancy force to all nodes in this segment that are submerged (i.e. below the water level).
     */
    private void applyBuoyancyForce() {
        if (ventralNodeB.getPosition().getY() < OctopusArm.getWaterLevel())
            ventralNodeB.addForce(new Vector2f(0f, (float) waterWeight * ventralNodeB.getMass()));

        if (dorsalNodeB.getPosition().getY() < OctopusArm.getWaterLevel())
            dorsalNodeB.addForce(new Vector2f(0f, (float) waterWeight * ventralNodeB.getMass()));
    }

    /**
     * Computes the component force vector to apply a force from BodyB to BodyB
     */
    protected Vector2f computeImpulseVector(double force, Body a, Body b) {
        return computeImpulseVector(force, a.getPosition(), b.getPosition());
    }

    /**
     * Computes the component force vector to apply at the angle from Position1 to Position2
     */
    protected Vector2f computeImpulseVector(double force, ROVector2f a, ROVector2f b) {

        float x = (float) (force * Math.cos(Util.angleOf(a, b)));
        float y = (float) (force * Math.sin(Util.angleOf(a, b)));

        return new Vector2f(x, y);
    }

    /**
     * Compute the centroid (center of mass) as described by Paul Borke.
     * See: http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
     * <p/>
     * Make sure you have computed the area before calling this!
     *
     * @return the computed centroid
     */
    protected ROVector2f computeCentroid() {
        double x = 0, y = 0, area;
        ROVector2f v1, v2;

        area = getArea();

        v1 = dorsalNodeA.getPosition();
        v2 = ventralNodeA.getPosition();
        x += (v1.getX() + v2.getX()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());
        y += (v1.getY() + v2.getY()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());

        v1 = ventralNodeA.getPosition();
        v2 = ventralNodeB.getPosition();
        x += (v1.getX() + v2.getX()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());
        y += (v1.getY() + v2.getY()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());

        v1 = ventralNodeB.getPosition();
        v2 = dorsalNodeB.getPosition();
        x += (v1.getX() + v2.getX()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());
        y += (v1.getY() + v2.getY()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());

        v1 = dorsalNodeB.getPosition();
        v2 = dorsalNodeA.getPosition();
        x += (v1.getX() + v2.getX()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());
        y += (v1.getY() + v2.getY()) * (v1.getX() * v2.getY() - v2.getX() * v1.getY());

        return new Vector2f((float) (x / (6 * area)), (float) (y / (6 * area)));
    }

    /**
     * Check if the polygon is convex.  All angles must be less than or equal to 180 degrees.  Adapted from
     * {@link net.phys2d.raw.shapes.Polygon#isConvex()}. This method is protected to support JUnit testing.
     *
     * @return true iff this polygon is convex
     */
    protected boolean isConvex() {
        ROVector2f v1, v2, v3;

        // check if all angles are smaller or equal to 180 degrees
        v1 = dorsalNodeA.getPosition();
        v2 = ventralNodeA.getPosition();
        v3 = ventralNodeB.getPosition();
        // does the 3d cross product point up or down?
        if ((v3.getX() - v1.getX()) * (v2.getY() - v1.getY()) - (v2.getX() - v1.getX()) * (v3.getY() - v1.getY()) >= 0)
            return false;

        v1 = ventralNodeA.getPosition();
        v2 = ventralNodeB.getPosition();
        v3 = dorsalNodeB.getPosition();
        // does the 3d cross product point up or down?
        if ((v3.getX() - v1.getX()) * (v2.getY() - v1.getY()) - (v2.getX() - v1.getX()) * (v3.getY() - v1.getY()) >= 0)
            return false;

        v1 = ventralNodeB.getPosition();
        v2 = dorsalNodeB.getPosition();
        v3 = dorsalNodeA.getPosition();
        // does the 3d cross product point up or down?
        if ((v3.getX() - v1.getX()) * (v2.getY() - v1.getY()) - (v2.getX() - v1.getX()) * (v3.getY() - v1.getY()) >= 0)
            return false;

        v1 = dorsalNodeB.getPosition();
        v2 = dorsalNodeA.getPosition();
        v3 = ventralNodeA.getPosition();
        // does the 3d cross product point up or down?
        if ((v3.getX() - v1.getX()) * (v2.getY() - v1.getY()) - (v2.getX() - v1.getX()) * (v3.getY() - v1.getY()) >= 0)
            return false;

        return true;
    }

    /**
     * Returns a collection of all the {@link Body} objects in this {@link ArmSegment}.
     *
     * @return The collection of {@link Body} objects in the {@link ArmSegment}.
     * @see hyper.experiments.octopusArm.model.Simulatable#getAllBodies()
     */
    public Collection<Body> getAllBodies() {
        Collection<Body> bodies = new ArrayList<Body>();
        bodies.add(dorsalNodeB);
        bodies.add(ventralNodeB);
        return bodies;
    }

    private final double area;        // calculated during construction and then set as immutable
    private double pressure;
    private final double waterWeight = OctopusArm.getBuoyancyForce();

    private final Segment previous;
    private final Body dorsalNodeA;    // dorsal node A from previous arm segment
    private final Body ventralNodeA;    // ventral node A from previous arm segment
    private final Body dorsalNodeB;    // dorsal node B is my dorsal node
    private final Body ventralNodeB;    // venrtal node B is my ventral node

    protected final Muscle dorsalMuscle;    // dorsal muscle (simulated as a spring)
    protected final Muscle transverseMuscle;// transverse muscle (simulated as a spring)
    protected final Muscle ventralMuscle;    // ventral muscle (simulated as a spring)
}
