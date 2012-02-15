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

/**
 * @author Brian Woolley
 */
public abstract class RangeSensorArray {

    /**
     * Creates a range finder device.  The field of view spans from -Pi to Pi, with 0 being straight ahead.
     *
     * @param resolution  sets the number of readings over the field of view
     * @param maxRange    sets the maximum detection range for the device
     * @param fieldOfView sets the field of view, (e.g. 2Pi and Pi are common values)
     */
    public RangeSensorArray(int resolution, double maxRange, double fieldOfView) {
        assert (resolution > 0);
        f_resolution = resolution;
        assert (maxRange > 0);
        f_maxRange = maxRange;
        assert (fieldOfView > 0);

        while (fieldOfView < 0) fieldOfView += 2 * Math.PI;
        while (fieldOfView > 2 * Math.PI) fieldOfView -= 2 * Math.PI;
        f_fieldOfView = fieldOfView;

        f_UpperLimit = f_fieldOfView / 2;
        f_LowerLimit = -f_fieldOfView / 2;

        f_interval = f_fieldOfView / resolution;
    }

    /**
     * Generates a set of range values as seen by the sensor at segment i.
     *
     * @param state   the state of the arm
     * @param segment the segment where the sensor is mounted
     * @return the set of range values collected as an array of doubles
     */
    public double[] scan(ArmState state, int segment) {

        // Get the point (alpha) between the dorsal and ventral nodes at the tip
        ROVector2f self = state.getAlphaPositions().get(segment);

        // Get the orientation of the arm at alpha
        double orientation = state.getAlphaAngles().get(segment);

        // The angle to the center of the target
        double theta = Util.addAngles(Util.angleOf(self, state.getTargetPosition()), -orientation);

        // The range to the center of the target
        double dist = self.distance(state.getTargetPosition());

        return scan(dist, theta, state.getTarget());
    }

    protected abstract double[] scan(double dist, double theta, Target target);

    final int f_resolution;
    final double f_maxRange,
            f_fieldOfView,
            f_UpperLimit,
            f_LowerLimit,
            f_interval;
}
