package sneat;

import common.evolution.EvolutionaryAlgorithm;
import hyper.evaluate.SNEATExperiment;
import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IdGenerator;
import sneat.evolution.NeatParameters;
import sneat.neatgenome.GenomeFactory;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 8:26:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEAT implements EvolutionaryAlgorithm {
    final private SNEATExperiment exp;
    private EvolutionAlgorithm ea;

    private int generation;
    private int lastInnovation;
    private double bsf;

    public SNEAT(SNEATExperiment exp) {
        this.exp = exp;
    }

    public void initialGeneration() {
        generation = 1;
        lastInnovation = 0;
        bsf = -Double.MAX_VALUE;
        IdGenerator idgen = new IdGenerator();
        ea = new EvolutionAlgorithm(
                new sneat.evolution.Population(idgen,
                        GenomeFactory.createGenomeList(exp.getDefaultNeatParameters(),
                                idgen,
                                exp.getInputNeuronCount(),
                                exp.getOutputNeuronCount(),
                                exp.getDefaultNeatParameters().pInitialPopulationInterconnections,
                                exp.getDefaultNeatParameters().populationSize)
                ),
                exp.getPopulationEvaluator(),
                exp.getDefaultNeatParameters());
        checkIfInnovation();
    }

    public void nextGeneration() {
        generation++;
        ea.performOneGeneration();
        checkIfInnovation();
    }

    public void finished() {
    }

    public String getConfigString() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasImproved() {
        return lastInnovation == 0;
    }

    public int getGeneration() {
        return generation;
    }

    public int getEvaluations() {
        return getGeneration() * ea.getPopulation().getPopulationSize();
    }

    public int getLastInnovation() {
        return lastInnovation;
    }

    public NeatParameters getNeatParameters() {
        return exp.getDefaultNeatParameters();
    }

    public EvolutionAlgorithm getEA() {
        return ea;
    }

    private void checkIfInnovation() {
        if (ea.getBestGenome().getFitness() > bsf) {
            bsf = ea.getBestGenome().getFitness();
            lastInnovation = 0;
        } else {
            lastInnovation++;
        }
    }
}
