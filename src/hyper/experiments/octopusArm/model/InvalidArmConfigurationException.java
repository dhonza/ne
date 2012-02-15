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
 * created by Brian Woolley on Feb 16, 2011
 */
package hyper.experiments.octopusArm.model;

/**
 * This exception is thrown when an {@link ArmSegment} fails its convex check.
 *
 * @author Brian Woolley on Feb 16, 2011
 */
public class InvalidArmConfigurationException extends Exception {

    /**
     * Creates a new {@link InvalidArmConfigurationException} when an {@link ArmSegment} fails its convex check.
     */
    public InvalidArmConfigurationException() {
        super("Invalid arm configuration detected.");
    }

    /**
     * Creates a new {@link InvalidArmConfigurationException} with custom message.
     */
    public InvalidArmConfigurationException(String message) {
        super(message);
    }

    /**
     * System generated ID.
     */
    private static final long serialVersionUID = -4974087191476082242L;
}
