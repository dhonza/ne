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
import hyper.experiments.octopusArm.model.Target;
import net.phys2d.math.ROVector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class RenderPanel extends JPanel {

    public RenderPanel() {
    }

    public RenderPanel(Environment anEnvironment) {
        setEnvironment(anEnvironment);
    }

    private void setEnvironment(Environment aNewEnvironment) {
        assert (aNewEnvironment != null);
        env = aNewEnvironment;
    }

    public void renderArmPostion(Environment env) {
        setEnvironment(env);
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (env == null) {
            return;
        }

        g.setColor(Color.BLUE);

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(Panel_MAXX / 2, Panel_MAXY / 2);

        // Draw the target
        g2.draw(createEllipse2D(env.getArmState().getTarget()));

        List<ROVector2f> dorsal = env.getArmState().getDorsalPositions();
        List<ROVector2f> ventral = env.getArmState().getVentralPositions();
        Point2D.Float d, v;

        octopusSegments.clear();

        assert (dorsal.size() == ventral.size());
        for (int i = 0; i < dorsal.size(); i++) {
            d = translate(dorsal.get(i));
            v = translate(ventral.get(i));
            octopusSegments.add(new DVPair(d, v));
        }

        assert (env.getArmState().getSegmentCount() == octopusSegments.size());
        for (int i = 0; i < env.getArmState().getSegmentCount(); i++) {
            DVPair current = octopusSegments.get(i);

            if (i == 0) {
                // draw the base segment
                g2.draw(createThickLine(current.getDorsal(), current.getVentral(), 0));
            }
            if (i > 0) {
                Polygon poly;

                //draw the transverse muscle (line from dorsal to ventral)
                poly = createThickLine(current.getDorsal(), current.getVentral(), 4 * env.getArmState().getTransverseMuscleContraction(i));
                g2.fill(poly);
                g2.draw(poly);

                // draw the dorsal muscle (line from prev_dorsal to dorsal)
                poly = createThickLine(prevDorsal, current.getDorsal(), 4 * env.getArmState().getDorsalMuscleContraction(i));
                g2.fill(poly);
                g2.draw(poly);

                // draw the ventral muscle (line from prev_ventral to ventral)
                poly = createThickLine(prevVentral, current.getVentral(), 4 * env.getArmState().getVentralMuscleContraction(i));
                g2.fill(poly);
                g2.draw(poly);
            }

            //the current dorsal, ventral nodes will be the previous ones for next iteration.
            prevDorsal = current.getDorsal();
            prevVentral = current.getVentral();
        }

        //this.octopusSegments = null;
        prevDorsal = null;
        prevVentral = null;
    }

    private Polygon createThickLine(Point2D.Float from, Point2D.Float to, double thickness) {
        float x1 = from.x, y1 = from.y;
        float x2 = to.x, y2 = to.y;

        // The thick line is in fact a filled polygon
        float dX = x2 - x1;
        float dY = y2 - y1;

        // line length
        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double) (thickness) / (2 * lineLength);

        // The x,y increments from an endpoint needed to create a rectangle...
        double ddx = -scale * (double) dY;
        double ddy = scale * (double) dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;

        // Now we can compute the corner points...
        int xPoints[] = new int[4];
        int yPoints[] = new int[4];

        xPoints[0] = (int) x1 + dx;
        yPoints[0] = (int) y1 + dy;
        xPoints[1] = (int) x1 - dx;
        yPoints[1] = (int) y1 - dy;
        xPoints[2] = (int) x2 - dx;
        yPoints[2] = (int) y2 - dy;
        xPoints[3] = (int) x2 + dx;
        yPoints[3] = (int) y2 + dy;

        return new Polygon(xPoints, yPoints, 4);
    }


    private Point2D.Float translate(ROVector2f p) {
        float x = (float) p.getX() * Panel_MAXX / 2;
        float y = (float) -p.getY() * Panel_MAXY / 2;
        return new Point2D.Float(x, y);
    }

    private class DVPair {
        private Point2D.Float dorsal;
        private Point2D.Float ventral;

        private DVPair(Point2D.Float dorsal, Point2D.Float ventral) {
            this.dorsal = dorsal;
            this.ventral = ventral;
        }

        private Point2D.Float getDorsal() {
            return dorsal;
        }

        private Point2D.Float getVentral() {
            return ventral;
        }
    }

    private Ellipse2D.Double createEllipse2D(Target target) {
        double x, y, dx, dy;
        x = Panel_MAXX / 2 * (target.getX() - target.getWidth() / 2);
        dx = Panel_MAXX / 2 * target.getWidth();

        y = Panel_MAXY / -2 * (target.getY() + target.getHeight() / 2);
        dy = Panel_MAXY / 2 * target.getHeight();

        return new Ellipse2D.Double(x, y, dx, dy);
    }


    private Point2D.Float prevDorsal;
    private Point2D.Float prevVentral;
    private ArrayList<DVPair> octopusSegments = new ArrayList<DVPair>();
    private Environment env;

    private static final int Panel_MAXX = 500;
    private static final int Panel_MAXY = 500;

    private static final long serialVersionUID = -1320986463139329339L;
}
