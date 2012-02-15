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
 * created by Brian Woolley on Feb 15, 2011
 */
package hyper.experiments.octopusArm.model;

/**
 * The agent interface is a strategy pattern that allows the {@link Simulation} to run many types
 * of OctopusArm controllers without the need to know the details of their implementation, only that
 * they have implement this interface.
 *
 * @author Brian Woolley on Feb 15, 2011
 */
public interface Agent {

    /**
     * Agents must be able to provide an action recommendation based on the current {@link ArmState}.
     * <p/>
     * For more information, see Unified behavior framework for reactive robot control, Journal of Intelligent & Robotic
     * Systems, 55(2-3), by Brian G. Woolley and Gilbert L. Peterson at <href>http://www.springerlink.com/content/8468r442420x47t6/</href>
     *
     * @param currentState The current {@link ArmState}.
     * @return The recommended action to take based on the current {@link ArmState}.
     */
    public Action genAction(ArmState currentState);

    /**
     * Agents will be scored on their performance at a specific task.
     *
     * @param additionalFitness The fitness score earned by the agent.
     */
    public void addFitnessValue(int additionalFitness);

    /**
     * Agents typically have an identifying name, index, or ID.
     *
     * @return The name, index, or ID of the the agent.
     */
    public String getID();

}
