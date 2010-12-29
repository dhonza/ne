/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package robots.vivae.arena.parts;


/**
 * @author Petr Smejkal
 */
public abstract class Fixed extends Passive {
    
    public Fixed(float x, float y) {
        super(x, y);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString(){
        return "Fixed VivaeObject at " + "[" + getX() + ", " + getY() + "]";
    }
    
    @Override
    public void moveComponent() {
        direction = body.getRotation();
        x = body.getPosition().getX();
        y = body.getPosition().getY();
    }
    

}

