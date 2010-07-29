package neat;

import common.evolution.IDistance;
import common.net.linked.Link;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 12:28:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenomeDistance implements IDistance<Genome> {
    /**
     * Computes the distance of two Genomes. It uses the equation: <br>
     * <img src="../pic/eq_dist.gif">
     * <p/>
     * Where <i>E </i> is the number of excess genes, <i>D </i> the number of
     * disjoint genes and <i>W </i> is the average weight difference of matching
     * genes (including disabled). <i>c1 </i>- <i>3 </i> are the constants (
     * <b>NE.DISTANCE_C1 </b>- <b>3 </b>).
     *
     * @param a the first genome
     * @param b the second genome
     * @return the distance between this and <i>oother </i> Genome
     */
    public double distance(Genome a, Genome b) {
        int disjoint = 0, excess;
        int common = 0; // how many genes have the same innovation numbers
        double wDif = 0.0; // weight difference
        double actDif = 0.0; // activation function difference

        int i = 0, j = 0;
        Link il, jl;

        int iLen = a.net.getNumLinks(), jLen = b.net.getNumLinks();
        int larger = iLen; // number of genes of the larger Genome

        while ((i < iLen) && (j < jLen)) {
            il = a.linkGenes[i].link;
            jl = b.linkGenes[j].link;
            if (il.getId() == jl.getId()) {
                i++;
                j++;
                common++;
                wDif += Math.abs(il.getWeight() - jl.getWeight());
                if (il.getOut().getActivation() != jl.getOut().getActivation()) {
                    actDif += 1.0;
                }
            } else if (il.getId() > jl.getId()) {
                disjoint++;
                j++;
            } else {
                disjoint++;
                i++;
            }
        }
        if (i < iLen) {
            excess = iLen - i;
        } else {
            excess = jLen - j;
            larger = jLen;
        }

        // System.out.println( "C:" + common +" D:" + disjoint + " E:" + excess
        // + " W" + wDif/common);
        /** TODO set N = 1 for short genomes */
        // if( larger < 20 ) larger = 1;
        if (larger < 50) {
            larger = 1;
        }
        return ((NEAT.getConfig().distanceC1 * excess + NEAT.getConfig().distanceC2 * disjoint) / larger + (NEAT.getConfig().distanceC3 * wDif) / common + NEAT.getConfig().distanceCActivation * actDif);
    }
}
