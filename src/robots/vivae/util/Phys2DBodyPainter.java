/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package robots.vivae.util;

import java.awt.Color;
import java.awt.Graphics2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.shapes.Polygon;

/**
 * This class only serves as a debug tool. It can paint any Phys2D Body to see where it actually is.
 * @author Petr Smejkal
 */
public class Phys2DBodyPainter {

    /**
     * Gets a Phys2D body, determines it's type and paints it.
     * @param g Graphics where the body should be painted on.
     * @param body The Phys2D body to be painted.
     */
    public static void drawBody(Graphics2D g, Body body) {
		if (body.getShape() instanceof Box) {
			drawBoxBody(g,body,(Box) body.getShape());
		}
		if (body.getShape() instanceof Circle) {
			drawCircleBody(g,body,(Circle) body.getShape());
		}
		if (body.getShape() instanceof Line) {
			drawLineBody(g,body,(Line) body.getShape());
		}
		if (body.getShape() instanceof Polygon) {
			drawPolygonBody(g,body,(Polygon) body.getShape());
		}
	}

        /**
         * Paints a Polygon Phys2D body.
         * @param g Graphics where the body should be painted on.
         * @param body The Phys2D body to be painted.
         * @param poly a Ploygon it represents.
         */
	public static void drawPolygonBody(Graphics2D g, Body body, Polygon poly) {
            g.setColor(Color.black);
            ROVector2f[] verts = poly.getVertices(body.getPosition(), body.getRotation());
            for ( int i = 0, j = verts.length-1; i < verts.length; j = i, i++ ) {			
                g.drawLine(
                    (int) (0.5f + verts[i].getX()),
                    (int) (0.5f + verts[i].getY()), 
                    (int) (0.5f + verts[j].getX()),
                    (int) (0.5f + verts[j].getY()));
            }
	}

         /**
         * Paints a Line Phys2D body.
         * @param g Graphics where the body should be painted on.
         * @param body The Phys2D body to be painted.
         * @param line a Line it represents.
         */
	public static void drawLineBody(Graphics2D g, Body body, Line line) {
            g.setColor(Color.black);
            Vector2f[] verts = line.getVertices(body.getPosition(), body.getRotation());
            g.drawLine(
                (int) verts[0].getX(),
                (int) verts[0].getY(), 
                (int) verts[1].getX(),
                (int) verts[1].getY());
	}

       /**
         * Paints a Circle Phys2D body.
         * @param g Graphics where the body should be painted on.
         * @param body The Phys2D body to be painted.
         * @param circle a Cicrcle it represents.
         */
	public static void drawCircleBody(Graphics2D g, Body body, Circle circle) {
            g.setColor(Color.RED);
            float x = body.getPosition().getX();
            float y = body.getPosition().getY();
            float r = circle.getRadius();
            float rot = body.getRotation();
            float xo = (float) (Math.cos(rot) * r);
            float yo = (float) (Math.sin(rot) * r);
            g.drawOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
            g.drawLine((int) x,(int) y,(int) (x+xo),(int) (y+yo));
	}

        /**
         * Paints a Box Phys2D body.
         * @param g Graphics where the body should be painted on.
         * @param body The Phys2D body to be painted.
         * @param box a Box it represents.
         */
        
        public static void drawBoxBody(Graphics2D g, Body body, Box box) {
            Vector2f[] pts = box.getPoints(body.getPosition(), body.getRotation());
            Vector2f v1 = pts[0];
            Vector2f v2 = pts[1];
            Vector2f v3 = pts[2];
            Vector2f v4 = pts[3];
            g.setColor(Color.black);
            g.drawLine((int) v1.x,(int) v1.y,(int) v2.x,(int) v2.y);
            g.drawLine((int) v2.x,(int) v2.y,(int) v3.x,(int) v3.y);
            g.drawLine((int) v3.x,(int) v3.y,(int) v4.x,(int) v4.y);
            g.drawLine((int) v4.x,(int) v4.y,(int) v1.x,(int) v1.y);
	}

}

