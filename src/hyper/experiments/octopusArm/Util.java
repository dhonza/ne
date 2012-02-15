/**
 * Copyright (C) 2010 Brian Woolley
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
 * created by Brian Woolley on Feb 21, 2011
 */
package hyper.experiments.octopusArm;

import net.phys2d.math.ROVector2f;

/**
 * @author Brian Woolley on Feb 21, 2011
 */
public class Util {

    /**
     * Calculates the radian angle from point a to point b .
     *
     * @param a The reference point.
     * @param b Another point in the space.
     * @return The radian angle from point a to point b.
     */
    public static double angleOf(ROVector2f a, ROVector2f b) {
        // Calculate angle from a to b
        double x = b.getX() - a.getX();
        double y = b.getY() - a.getY();
        return Math.atan2(y, x);
    }

    /**
     * Adds two radian angles and returns a value between -Pi and Pi.
     *
     * @param a The first radian angle.
     * @param b Another radian angle.
     * @return The sum of two radian angles, normalized to a value between -Pi and Pi.
     */
    public static double addAngles(double a, double b) {
        return Util.normalize(a + b);
    }

    /**
     * Adjusts radian angles to be values between -Pi and Pi.
     *
     * @param raw The raw radian angle to be adjusted.
     * @return The adjusted radian angle (values are between -Pi and Pi).
     */
    public static double normalize(double raw) {
        while (raw < -Math.PI) raw += 2 * Math.PI;
        while (raw > Math.PI) raw -= 2 * Math.PI;
        return raw;
    }

}
