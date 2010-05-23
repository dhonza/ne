package neat;

import common.evolution.ProgressPrinter;

/**
 * User: honza
 * Date: Apr 10, 2006
 * Time: 8:45:57 PM
 */

public class NEATBasicProgressPrinter implements ProgressPrinter {
    protected final Population pop;

    public NEATBasicProgressPrinter(NEAT neat) {
        this.pop = neat.getPopulation();
    }

    public void printGeneration() {
        System.out.println("G:" + pop.getGeneration() +
                " EVA:" + pop.getEvaluations() +
                " BSF:" + pop.getBestSoFar().getFitness() +
                " BOG:" + pop.getBestOfGeneration().getFitness() +
                " LASTIN:" + pop.getLastInnovation() +
                " SPE:" + pop.getSpecies().size() +
                " DELTA:" + NEAT.getConfig().distanceDelta +
                " LIN:" + pop.getGlobalInnovation().getLinkInnovation() +
                " NIN:" + pop.getGlobalInnovation().getNeuronInnovation() +
                " BSFL:" + pop.getBestSoFar().getNet().getNumLinks() +
                " BSFHN:" + pop.getBestSoFar().getNet().getNumHidden()

        );
    }

    public void printProgress() {
        System.out.println(
                " NEW CHAMP:" + pop.getBestSoFar().getFitness() +
                        " BSFL:" + pop.getBestSoFar().getNet().getNumLinks() +
                        " BSFHN:" + pop.getBestSoFar().getNet().getNumHidden());
    }

    public void printFinished() {
        System.out.println("FINISHED");
    }
}