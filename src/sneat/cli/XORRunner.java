package sneat.cli;

import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IdGenerator;
import sneat.evolution.Population;
import sneat.experiments.HyperNEATParameters;
import sneat.experiments.IExperiment;
import sneat.experiments.XORExperiment;
import sneat.neatgenome.GenomeFactory;
import sneat.neatgenome.NeatGenome;
import sneat.neatgenome.xml.XmlGenomeWriterStatic;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

class XORRunner {
    private static Logger logger = Logger.getLogger("sneat.cli.runner");
    public static Formatter formatter = new Formatter() {
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    };

    XORRunner(String configFileName) throws IOException {
        initLogger();

        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(configFileName));

        System.out.println("PARAMETER SETTINGS: " + manager);

        for (ParameterCombination combination : manager) {
            System.out.println("PARAMETER COMBINATION: " + combination.toStringOnlyChannging());
            setActivationFunctions(combination);
            performExperiment(combination);
        }
    }

    private void setActivationFunctions(ParameterCombination parameters) {
        ActivationFunctionFactory.setSameProbabilitiesForList(parameters.getString("SNEAT.FUNCTIONS"));
    }

    private void performExperiment(ParameterCombination parameters) {
        IExperiment exp = new XORExperiment(parameters);
        IdGenerator idgen = new IdGenerator();

        EvolutionAlgorithm ea = new EvolutionAlgorithm(
                new Population(idgen,
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

        logger.info(exp.getDefaultNeatParameters().toString());

        double maxFitness = -Double.MAX_VALUE;
        for (int j = 0; j < exp.getDefaultNeatParameters().maxGenerations; j++) {
            long dt = System.currentTimeMillis();
            ea.performOneGeneration();
            if (ea.getBestGenome().getFitness() > maxFitness) {
                maxFitness = ea.getBestGenome().getFitness();
//                XmlGenomeWriterStatic.Write(new File("bestGenome" + j + ".xml"), (NeatGenome) ea.getBestGenome());
            }
            logger.info(ea.getGeneration() + " " + (maxFitness) + " " + (System.currentTimeMillis() - dt));

        }

        XmlGenomeWriterStatic.Write(new File("bestGenome.xml"), (NeatGenome) ea.getBestGenome(), ActivationFunctionFactory.getActivationFunction("NullFn"));


    }

    private void initLogger() throws IOException {
        Logger.getLogger("").getHandlers()[0].setFormatter(formatter);
        Handler fh = new FileHandler("sneat.log");
        fh.setFormatter(formatter);
        Logger.getLogger("").addHandler(fh);
    }

    public static void main(String[] args) throws IOException {
//        HyperNEATParameters.loadParameterFile();
        new XORRunner("cfg/sneat.properties");
    }
}
