package opt.sade;

import common.evolution.IProgressPrinter;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 4:50:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class SADEBasicProgressPrinter implements IProgressPrinter {
    final protected SADE sade;

    public SADEBasicProgressPrinter(SADE sade) {
        this.sade = sade;
    }

    public void printGeneration() {
        System.out.println("G: " + sade.getGeneration() +
                " EVA:" + sade.getEvaluations() +
                " BSF: " + sade.getMaxFitnessReached() +
                " BOG: " + sade.getBestOfGenerationFitness() +
                " LASTIN:" + sade.getLastInnovation()
        );
    }

    public void printProgress() {
        System.out.println(" NEW CHAMP:" + sade.getMaxFitnessReached());
    }

    public void printFinished() {
        System.out.println("FINISHED");
    }
}