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

import net.phys2d.raw.Body;

import java.util.Collection;

/**
 * Sets the required interface for objects participating in a stepped simulation
 */
public interface Simulatable {

    /**
     * Advance the object a single time step
     *
     * @throws Exception
     */
    public void step() throws InvalidArmConfigurationException;

    /**
     * Return the energy used at this segment for this time-step
     *
     * @return The forces begin applied
     */
    public double getEnergyUsed();

    /**
     * Provides all net.phys2d bodies contained in the <code>Simulatable</code> object
     *
     * @return a collection of all net.phys2d.raw.Body objects
     */
    public Collection<Body> getAllBodies();
}
