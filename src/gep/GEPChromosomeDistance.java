package gep;

import common.evolution.IDistance;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 12:41:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GEPChromosomeDistance implements IDistance<GEPChromosome> {
    public double distance(GEPChromosome a, GEPChromosome b) {
        return a.distance(b);
    }
}