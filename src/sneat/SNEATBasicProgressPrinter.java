package sneat;

import common.evolution.IProgressPrinter;
import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IGenome;

/**
 * User: honza
 * Date: Apr 10, 2006
 * Time: 8:45:57 PM
 */

public class SNEATBasicProgressPrinter implements IProgressPrinter {
    protected final SNEAT sneat;

    public SNEATBasicProgressPrinter(SNEAT sneat) {
        this.sneat = sneat;
    }

    public void printGeneration() {
        EvolutionAlgorithm ea = sneat.getEA();
        System.out.println("G:" + sneat.getGeneration() +
                " EVA:" + sneat.getEvaluations() +
                " BSF:" + ea.getBestGenome().getFitness() +
                " BOG:" + -Integer.MAX_VALUE +
                " LASTIN:" + sneat.getLastInnovation()
        );
    }

    public void printProgress() {
        EvolutionAlgorithm ea = sneat.getEA();
        IGenome best = ea.getBestGenome();
        System.out.println(
                " NEW CHAMP:" + best.getFitness()
        );
    }

    public void printFinished() {
        System.out.println("FINISHED");
    }
}