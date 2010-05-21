package gp;

import common.stats.Stats;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 4:50:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicProgressPrinter implements ProgressPrinter {
    protected GP gp;

    public void setGP(GP gp) {
        this.gp = gp;
    }

    public void printGeneration() {
        System.out.println("G: " + gp.getGeneration() + " BOG: " + gp.getBestOfGeneration().getFitness() + " BSF: " + gp.getBestSoFar().getFitness());
    }

    public void printProgress() {
//        Forest[] population = gp.getPopulation();
//        Arrays.sort(population);
//        for (Forest forest : population) {
//            System.out.println(forest);
//        }
        System.out.println("BSF: " + gp.getBestSoFar());
    }

    public void printFinished() {
        System.out.println("FINISHED");
        System.out.println("BSF: " + gp.getBestSoFar());
    }
}
