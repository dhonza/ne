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


/**
 * @author Brian Woolley
 *         An interface that describes a segment as a pair of nodes
 */
public interface Segment extends Simulatable {

    /**
     * Return the previous {@link Segment} associated with this {@link Segment}.
     *
     * @return The previous {@link Segment} (i.e. closer to the {@link ArmBase}).
     */
    public Segment getPreviousSegment();

    /**
     * Return the upper (dorsal) node for the segment.
     *
     * @return The segment's dorsal node.
     */
    public Body getDorsalNode();

    /**
     * Return the lower (ventral) node for the segment.
     *
     * @return The segment's ventral node.
     */
    public Body getVentralNode();

    /**
     * Calculates the midpoint between the dorsal and ventral nodes (alpha) at this timestep.
     *
     * @return The midpoint between the dorsal and ventral nodes.
     */
    public ROVector2f getAlpha();

    /**
     * Calculates the orientation of this segment.  The alpha angle is the angle perpendicular to the the transverse link
     * at this timestep.
     *
     * @return The current orientation of this segment.
     * @see Segment#getAlphaAngle()
     */
    public double getAlphaAngle();
}
