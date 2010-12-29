package robots.vivae.fitness;

import robots.vivae.arena.Arena;
import robots.vivae.arena.parts.Active;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 9, 2009
 * Time: 9:18:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class AverageSpeed extends FitnessFunction{
    Arena arena;
    public AverageSpeed(Arena arena){
        this.arena = arena;
    }

    public double getFitness(){
        double res=0d;
        Vector<Active> actives = arena.getActives();
        for (Iterator<Active> it = actives.iterator(); it.hasNext();) {
            Active ag = it.next();
            res+=ag.odometer;
        }
        return res/actives.size()/arena.stepsDone;
     }
}
