package opt.cmaes;

import common.evolution.IProgressPrinter;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 4:50:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMAESBasicProgressPrinter implements IProgressPrinter {
    final protected CMAES cmaes;

    public CMAESBasicProgressPrinter(CMAES cmaes) {
        this.cmaes = cmaes;
    }

    public void printGeneration() {
        System.out.println("G: " + cmaes.getGeneration() +
                " EVA:" + cmaes.getEvaluations() +
                " BSF: " + cmaes.getMaxFitnessReached() +
                " BOG: " + cmaes.getBestOfGenerationFitness() +
                " LASTIN:" + cmaes.getLastInnovation()
        );
    }

    public void printProgress() {
        System.out.println(" NEW CHAMP:" + cmaes.getMaxFitnessReached());
    }

    public void printFinished() {
        System.out.println("FINISHED");
    }
}