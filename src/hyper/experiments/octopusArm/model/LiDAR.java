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

/**
 * @author Brian Woolley
 */
public class LiDAR extends RangeSensorArray {

    /**
     * Creates a LASER range finder device.  The field of view spans from -Pi to Pi, with 0 being straight ahead.
     *
     * @param resolution sets the number of range readings collected, where resolution > 0.
     */
    public LiDAR(int resolution, double maxRange) {
        this(resolution, maxRange, 2 * Math.PI);
    }

    public LiDAR(int resolution, double maxRange, double fieldOfView) {
        super(resolution, maxRange, fieldOfView);
    }

    /**
     * @see hyper.experiments.octopusArm.model.RangeSensorArray#scan(double, double, Target)
     */
    protected double[] scan(double dist, double theta, Target target) {
        double[] result = new double[f_resolution];
        double a, err, opp, diff, range;
        for (int i = 0; i < f_resolution; i++) {

            if (f_resolution % 2 == 0) {                // Resolution is even...
                a = f_UpperLimit - i * f_interval;
            } else {                                // Resolution is odd...
                a = f_UpperLimit - f_interval / 2 - i * f_interval;
            }
            err = Math.abs(a - theta);
            opp = dist * Math.tan(err);
            if (err > Math.tan(target.getRadius() / dist)) {
                result[i] = f_maxRange;
            } else {
                // Provides a good approximation for a round target surface
                diff = target.getRadius() * Math.cos(opp / target.getRadius() * (Math.PI / 2));
                range = dist - diff;
                result[i] = range;
            }
        }
        return result;
    }
}
