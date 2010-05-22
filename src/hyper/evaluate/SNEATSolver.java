package hyper.evaluate;

import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.NetSubstrateBuilder;
import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IdGenerator;
import sneat.experiments.HyperNEATParameters;
import sneat.neatgenome.GenomeFactory;
import sneat.neatgenome.NeatGenome;
import sneat.neatgenome.xml.XmlGenomeWriterStatic;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * mela by dostat z venku: parametry NEAT, progress printer (stejny pro GP)
 */
public class SNEATSolver implements Solver {
    final private ParameterCombination parameters;
    final private NetSubstrateBuilder substrateBuilder;
    final private Stats stats;
    final private Problem problem;

    public SNEATSolver(ParameterCombination parameters, NetSubstrateBuilder substrateBuilder, Stats stats, Problem problem) {
        this.parameters = parameters;
        this.substrateBuilder = substrateBuilder;
        this.stats = stats;
        this.problem = problem;
    }

    public void solve() {
        //TODO encapsulate!, je jeste v evaluatoru!
        int inputsCPPN = 2 * substrateBuilder.getSubstrate().getMaxDimension();
        int outputsCPPN = substrateBuilder.getSubstrate().getNumOfConnections();

        HyperNEATParameters.loadParameterFile();
        SNEATExperiment exp = new SNEATExperiment(parameters, substrateBuilder, problem, inputsCPPN, outputsCPPN);
        IdGenerator idgen = new IdGenerator();

        EvolutionAlgorithm ea = new EvolutionAlgorithm(
                new sneat.evolution.Population(idgen,
                        GenomeFactory.CreateGenomeList(exp.getDefaultNeatParameters(),
                                idgen,
                                exp.getInputNeuronCount(),
                                exp.getOutputNeuronCount(),
                                exp.getDefaultNeatParameters().pInitialPopulationInterconnections,
                                exp.getDefaultNeatParameters().populationSize)
                ),
                exp.getPopulationEvaluator(),
                exp.getDefaultNeatParameters());

        XmlGenomeWriterStatic.Write(new File("seedGenome.xml"), (NeatGenome) ea.getPopulation().getGenomeList().get(0));

        double maxFitness = -Double.MAX_VALUE;
        for (int j = 0; j < exp.getDefaultNeatParameters().maxGenerations; j++) {
            long dt = System.currentTimeMillis();
            ea.performOneGeneration();
            if (ea.getBestGenome().getFitness() > maxFitness) {
                maxFitness = ea.getBestGenome().getFitness();
//                XmlGenomeWriterStatic.Write(new File("bestGenome" + j + ".xml"), (NeatGenome) ea.getBestGenome());
            }
            System.out.println(ea.getGeneration() + " " + (maxFitness) + " " + (System.currentTimeMillis() - dt));
        }
        XmlGenomeWriterStatic.Write(new File("bestGenome.xml"), (NeatGenome) ea.getBestGenome(), ActivationFunctionFactory.getActivationFunction("NullFn"));


        /*
        NEATEvaluator evaluator = new NEATEvaluator(substrateBuilder, problem);

        NEAT aNEAT = new NEAT();
        NEATConfig config = NEAT.getConfig();
        config.populationSize = 1000;
        config.lastGeneration = 15000;
        config.netWeightsAmplitude = 10.0;
        config.targetFitness = problem.getTargetFitness();

        FitnessSharingPopulation population = new FitnessSharingPopulation(evaluator, getPrototype(inputsCPPN, outputsCPPN));

        aNEAT.setPopulation(population);
        ProgressPrinter progressPrinter = new NetProgressPrinter1D(population, substrateBuilder.getSubstrate(), problem);
        aNEAT.setProgressPrinter(progressPrinter);
        aNEAT.run(false);

        stats.addSample("STAT_GENERATIONS", population.getGeneration());
        */
    }
}