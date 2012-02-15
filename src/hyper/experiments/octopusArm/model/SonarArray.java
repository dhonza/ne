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

/**
 * Sonar arrays operate by reporting the distance to the target surface when the {@link Target} is within a angular (pie-slice) region.
 *
 * @author Brian Woolley
 */
public class SonarArray extends RangeSensorArray {

    /**
     * Creates an array of sonar range finder devices that detect the target (surface or center).  In the surface mode the size
     * of the target matters and more than one sensor may actively report the distance to the target at the same time.  In the
     * target center mode, the sonar devices only detect the target when the target center falls within the pie-slice region.
     * <p/>
     * The surface mode provides a more real-world scenario where the target may be visible to a number of sensors simultaneously.
     * The target center mode provides a simple situation where the target is only detected by one sensor at a time, but may result
     * in sudden changes that may adversely affect controller behaviors.
     * <p/>
     * The field of view spans from -Pi to Pi, with 0 being straight ahead.
     *
     * @param resolution     The number of range readings collected, where resolution > 0.
     * @param maxRange       The maximum effective range of the sensor.
     * @param centerlineOnly Detect the target surface (when FALSE) or detect the target center only (when TRUE).
     * @see RangeSensorArray#RangeSensorArray(int, double, double)
     */
    public SonarArray(int resolution, double maxRange, boolean centerlineOnly) {
        super(resolution, maxRange, 2 * Math.PI);
        f_CenterlineOnly = centerlineOnly;
    }

    /**
     * @see hyper.experiments.octopusArm.model.RangeSensorArray#scan(double, double, Target)
     */
    protected double[] scan(double dist, double theta, Target target) {
        double delta;                    // The +/- angle to the edges of the target
        if (f_CenterlineOnly) delta = 0;
        else delta = Math.atan(target.getRadius() / dist);

        double[] result = new double[f_resolution];
        double upper, lower;
        double alpha = Util.normalize(theta + delta);
        double beta = Util.normalize(theta - delta);

        if (f_resolution % 2 == 0) {
            // Resolution is even...
            upper = Math.PI - f_interval / 2;
            lower = upper - f_interval;
        } else {
            // Resolution is odd...
            upper = Math.PI;
            lower = upper - f_interval;
        }

        for (int i = 0; i < f_resolution; i++) {
            if (alpha > lower && beta < upper)
                result[i] = dist;
            else
                result[i] = f_maxRange;
            upper = lower;
            Util.normalize(lower -= f_interval);
        }
        return result;
    }

    private final boolean f_CenterlineOnly;
}
