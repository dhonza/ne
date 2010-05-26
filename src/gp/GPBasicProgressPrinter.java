package gp;

import common.evolution.ProgressPrinter;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 4:50:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPBasicProgressPrinter implements ProgressPrinter {
    final protected GP gp;

    public GPBasicProgressPrinter(GP gp) {
        this.gp = gp;
    }

    public void printGeneration() {
        System.out.println("G: " + gp.getGeneration() +
                " EVA:" + gp.getEvaluations() +
                " BSF: " + gp.getBestSoFar().getFitness() +
                " BOG: " + gp.getBestOfGeneration().getFitness() +
                " LASTIN:" + gp.getLastInnovation()
        );
    }

    public void printProgress() {
//        Forest[] population = gp.getPopulation();
//        Arrays.sort(population);
//        for (Forest forest : population) {
//            System.out.println(forest);
//        }
        System.out.println(" NEW CHAMP:" + gp.getBestSoFar());
    }

    public void printFinished() {
        System.out.println("FINISHED");
    }
}