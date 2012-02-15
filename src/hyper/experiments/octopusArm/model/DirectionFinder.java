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
 * @author Brian Woolley
 */
public class DirectionFinder extends RangeSensorArray {

    /**
     * Creates an array of sonar range finder devices.  The field of view spans from -Pi to Pi, with 0 being straight ahead.
     *
     * @param resolution sets the number of range readings collected, where resolution > 0.
     * @see RangeSensorArray#RangeSensorArray(int, double, double)
     */
    public DirectionFinder(int resolution, double maxRange) {
        super(resolution, maxRange, 2 * Math.PI);
    }

    /**
     * @see hyper.experiments.octopusArm.model.RangeSensorArray#scan(double, double, Target)
     */
    protected double[] scan(double dist, double theta, Target target) {
        double[] result = new double[f_resolution];
        double upper, lower;

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
            if (theta > lower && theta < upper)
                result[i] = dist;
            else
                result[i] = f_maxRange;
            upper = lower;
            Util.normalize(lower -= f_interval);
        }
        return result;
    }
}
