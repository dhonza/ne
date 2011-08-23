package gpat.distance;

import common.evolution.IDistance;
import gpat.ATLinkGene;
import gpat.ATTree;
import gpat.GPAT;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATTreeDistance implements IDistance<ATTree> {
    public double distance(ATTree a, ATTree b) {
        int disjoint = 0, excess;
        int common = 0; // how many genes have the same innovation numbers
        double wDif = 0.0; // weight difference
        double actDif = 0.0; // activation function difference

        int i = 0, j = 0;
        ATLinkGene il, jl;

        int iLen = a.getLinkGenesList().size(), jLen = b.getLinkGenesList().size();

        while ((i < iLen) && (j < jLen)) {
            il = a.getLinkGenesList().get(i);
            jl = b.getLinkGenesList().get(j);
            if (il.getInnovation() == jl.getInnovation()) {
                i++;
                j++;
                common++;
                double iConstant = il.getTo().getConstantForLinkGene(il);
                double jConstant = jl.getTo().getConstantForLinkGene(jl);
                wDif += Math.abs(iConstant - jConstant);
            } else if (il.getInnovation() > jl.getInnovation()) {
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
        }
        //TODO different for large genes: see neat.GenomeDistance
        //TODO involve locks?

        double weights = (GPAT.DISTANCE_C3 * wDif) / common;
        if (Double.isNaN(weights)) {
            weights = 0.0;
        }
        //TODO should be proportional to to length of a longer genome!!!
        double distance = GPAT.DISTANCE_C1 * excess + GPAT.DISTANCE_C2 * disjoint + weights;
        return distance;
    }

}
