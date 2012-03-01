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
package hyper.experiments.octopusArm;

import hyper.experiments.octopusArm.model.Action;
import hyper.experiments.octopusArm.model.Agent;
import hyper.experiments.octopusArm.model.ArmState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Woolley on May 6, 2010
 */
public class KeyboardAgent implements Agent, KeyListener {

    public KeyboardAgent() {
        init();
    }

    public Action genAction(ArmState state) {
        return currentAction;
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("Pressed key " + e.toString());
        //nothing to do here.
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("Released key " + e.toString());
        //nothing to do here.
    }

    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (cmd.containsKey(c)) {
            System.out.print(cmd.get(c).type.toString() + "(" + cmd.get(c).seg + ") = ");
            cmd.get(c).toggle();
        } else {
            System.out.println("unknown input '" + c + "'");
        }
    }

    public void addFitnessValue(int additionalFitness) {
        myFitness += additionalFitness;
        System.out.println("Fitness = " + myFitness);
    }

    public String getID() {
        return "Human Interface Agent";
    }

    private void init() {
        cmd.put('q', new UserCmd(1, Muscle.DORSAL));
        cmd.put('a', new UserCmd(1, Muscle.TRANS));
        cmd.put('z', new UserCmd(1, Muscle.VENTRAL));

        cmd.put('w', new UserCmd(2, Muscle.DORSAL));
        cmd.put('s', new UserCmd(2, Muscle.TRANS));
        cmd.put('x', new UserCmd(2, Muscle.VENTRAL));

        cmd.put('e', new UserCmd(3, Muscle.DORSAL));
        cmd.put('d', new UserCmd(3, Muscle.TRANS));
        cmd.put('c', new UserCmd(3, Muscle.VENTRAL));

        cmd.put('r', new UserCmd(4, Muscle.DORSAL));
        cmd.put('f', new UserCmd(4, Muscle.TRANS));
        cmd.put('v', new UserCmd(4, Muscle.VENTRAL));

        cmd.put('t', new UserCmd(5, Muscle.DORSAL));
        cmd.put('g', new UserCmd(5, Muscle.TRANS));
        cmd.put('b', new UserCmd(5, Muscle.VENTRAL));

        cmd.put('y', new UserCmd(6, Muscle.DORSAL));
        cmd.put('h', new UserCmd(6, Muscle.TRANS));
        cmd.put('n', new UserCmd(6, Muscle.VENTRAL));

        cmd.put('u', new UserCmd(7, Muscle.DORSAL));
        cmd.put('j', new UserCmd(7, Muscle.TRANS));
        cmd.put('m', new UserCmd(7, Muscle.VENTRAL));

        cmd.put('i', new UserCmd(8, Muscle.DORSAL));
        cmd.put('k', new UserCmd(8, Muscle.TRANS));
        cmd.put(',', new UserCmd(8, Muscle.VENTRAL));

        cmd.put('o', new UserCmd(9, Muscle.DORSAL));
        cmd.put('l', new UserCmd(9, Muscle.TRANS));
        cmd.put('.', new UserCmd(9, Muscle.VENTRAL));

        cmd.put('p', new UserCmd(10, Muscle.DORSAL));
        cmd.put(';', new UserCmd(10, Muscle.TRANS));
        cmd.put('/', new UserCmd(10, Muscle.VENTRAL));
    }

    private class UserCmd {
        private boolean active;
        private double force;
        private Muscle type;
        private int seg;

        private UserCmd(int i, Muscle dtv) {
            active = false;
            force = 0;
            seg = i;
            type = dtv;
        }

        private void toggle() {
            if (active) {
                active = false;
                force = 0.0;
            } else {
                active = true;
                force = 1.0;
            }

            switch (type) {
                case DORSAL:
                    currentAction.setDorsalMuscleContraction(seg, force);
                    break;
                case TRANS:
                    currentAction.setTransverseMuscleContraction(seg, force);
                    break;
                case VENTRAL:
                    currentAction.setVentralMuscleContraction(seg, force);
                    break;
                default:
                    System.out.println("No effect is associated with " + type.toString());
            }
            System.out.println(force);
        }
    }

    private Action currentAction = new Action();
    private Map<Character, UserCmd> cmd = new HashMap<Character, UserCmd>();

    private enum Muscle {DORSAL, TRANS, VENTRAL}

    private int myFitness = 0;
}
