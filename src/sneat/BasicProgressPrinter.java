package sneat;

import common.evolution.ProgressPrinter;
import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IGenome;

/**
 * User: honza
 * Date: Apr 10, 2006
 * Time: 8:45:57 PM
 */

public class BasicProgressPrinter implements ProgressPrinter {
    protected final SNEAT sneat;

    public BasicProgressPrinter(SNEAT sneat) {
        this.sneat = sneat;
    }

    /**
     * Prints info. This method is always called when a new generation is
     * created. It can be overriden to perform drawing, writing to files etc..
     */
    public void printGeneration() {
        System.out.println(toStringGeneration());
    }

    public String toStringGeneration() {
        EvolutionAlgorithm ea = sneat.getEA();
        return "G:" + ea.getGeneration() + " F:" + ea.getBestGenome().getFitness();
    }

    /**
     * Prints info only when population improves. This method is always called
     * when a new generation is created. It can be overridden to perform
     * drawing, writing to files etc..
     */
    public void printProgress() {
        System.out.println(toStringProgress());
    }

    public void printFinished() {
        System.out.println("FINISHED");
    }

    public String toStringProgress() {
        EvolutionAlgorithm ea = sneat.getEA();
        IGenome best = ea.getBestGenome();
        return " NEW CHAMP:" + best.getFitness();
    }
}