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
package hyper.experiments.octopusArm.userInterface;

import hyper.experiments.octopusArm.model.Environment;

import javax.swing.*;
import java.awt.*;

/**
 * @author Brian Woolley
 */
public class SimulationViewer extends JFrame implements Renderable {

    public SimulationViewer() {
        super("OctopusArm Simulation Viewer");

        getContentPane().add(f_panel, BorderLayout.CENTER);
        setSize(MAXX, MAXY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void renderArmPostion(Environment env) {
        f_panel.renderArmPostion(env);
        try {
            Thread.sleep(Math.max(0, hack - System.currentTimeMillis()));
        } catch (Exception e) {
        }
        hack = System.currentTimeMillis() + 100;
    }

    private long hack = System.currentTimeMillis();

    private final RenderPanel f_panel = new RenderPanel();

    private static final int MAXX = 500;
    private static final int MAXY = 525;
    private static final long serialVersionUID = -6809296913716850682L;
}
