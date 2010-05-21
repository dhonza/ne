package neat;

/**
 * User: honza
 * Date: Apr 10, 2006
 * Time: 8:45:57 PM
 */

//TODO - nejedna se v podstate o listener?
public class BasicProgressPrinter extends ProgressPrinter {
    public BasicProgressPrinter(Population pop) {
        super(pop);
    }

    /**
     * Prints info. This method is always called when a new generation is
     * created. It can be overriden to perform drawing, writing to files etc..
     */
    public void printGeneration() {
        System.out.println(toStringGeneration());
    }

    public String toStringGeneration(){
         return "G:" + pop.getGeneration() + " EVA:" + pop.getEvaluation() + " SPE:" + pop.getSpecies().size() + " BSF:" + pop.getBestSoFar().getFitness() + " BOG:"
                + pop.getBestOfGeneration().getFitness() + " LASTIN:" + pop.getLastInnovation() + " DELTA:" + NEAT.getConfig().distanceDelta + " LIN:"
                + pop.getGlobalInnovation().getLinkInnovation() + " NIN:" + pop.getGlobalInnovation().getNeuronInnovation() + " BSFL:" + pop.getBestSoFar().getNet().getNumLinks()
                + " BSFHN:" + pop.getBestSoFar().getNet().getNumHidden();
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

    public String toStringProgress(){
        return " NEW CHAMP:" + pop.getBestSoFar().getFitness() + " BSFL:" + pop.getBestSoFar().getNet().getNumLinks() + " BSFHN:"
                + pop.getBestSoFar().getNet().getNumHidden();
    }
}