package sneat.cli;

import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IdGenerator;
import sneat.evolution.Population;
import sneat.experiments.IExperiment;
import sneat.experiments.skrimish.SkirmishExperiment;
import sneat.experiments.skrimish.SkirmishNetworkEvaluator;
import sneat.neatgenome.GenomeFactory;
import sneat.neatgenome.IdGeneratorFactory;
import sneat.neatgenome.NeatGenome;
import sneat.neatgenome.xml.XmlGenomeWriterStatic;
import sneat.neatgenome.xml.XmlNeatGenomeReaderStatic;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;

import java.io.*;

class Program {
    public static void main(String[] args) {
        {
            String folder = "";
            NeatGenome seedGenome = null;
            String filename = null;
            String shape = "triangle";
            boolean isMulti = false;

            for (int j = 0; j < args.length; j++) {
                if (j <= args.length - 2)
                    if (args[j].equals("-seed")) {
                        filename = args[++j];
                        System.out.println("Attempting to use seed from file " + filename);
                    } else if (args[j].equals("-folder")) {
                        folder = args[++j];
                        System.out.println("Attempting to output to folder " + folder);
                    } else if (args[j].equals("-shape")) {
                        shape = args[++j];
                        System.out.println("Attempting to do experiment with shape " + shape);
                    } else if (args[j].equals("-multi")) {
                        isMulti = Boolean.valueOf(args[++j]);
                        System.out.println("Experiment is heterogeneous? " + isMulti);
                    }
            }

            if (filename != null) {
                seedGenome = XmlNeatGenomeReaderStatic.Read(new File(filename));
            }

            double maxFitness = 0;
            int maxGenerations = 1000;
            int populationSize = 150;
            int inputs = 4;
            IExperiment exp = new SkirmishExperiment(inputs, 1, isMulti, shape);

            PrintStream sw = null;
            try {
                sw = new PrintStream(new BufferedOutputStream(new FileOutputStream(folder + "logfile.txt")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }

            IdGenerator idgen;
            EvolutionAlgorithm ea;
            if (seedGenome == null) {
                idgen = new IdGenerator();
                ea = new EvolutionAlgorithm(
                        new Population(idgen,
                                GenomeFactory.CreateGenomeList(exp.getDefaultNeatParameters(),
                                        idgen,
                                        exp.getInputNeuronCount(),
                                        exp.getOutputNeuronCount(),
                                        exp.getDefaultNeatParameters().pInitialPopulationInterconnections,
                                        populationSize)
                        ),
                        exp.getPopulationEvaluator(),
                        exp.getDefaultNeatParameters());

            } else {
                idgen = new IdGeneratorFactory().CreateIdGenerator(seedGenome);
                ea = new EvolutionAlgorithm(new Population(idgen, GenomeFactory.CreateGenomeList(seedGenome, populationSize, exp.getDefaultNeatParameters(), idgen)), exp.getPopulationEvaluator(), exp.getDefaultNeatParameters());
            }

            System.out.println(exp.getDefaultNeatParameters().toString());

            XmlGenomeWriterStatic.Write(new File(folder + "seedGenome.xml"),
                    (NeatGenome) ea.getPopulation().getGenomeList().get(0));
            XmlGenomeWriterStatic.Write(new File(folder + "seedNetwork.xml"),
                    SkirmishNetworkEvaluator.substrate.generateMultiGenomeModulus(ea.getPopulation().getGenomeList().get(0).decode(null), 5));

            for (int j = 0; j < maxGenerations; j++) {
                long dt = System.currentTimeMillis();
                ea.performOneGeneration();
                if (ea.getBestGenome().getFitness() > maxFitness) {
                    maxFitness = ea.getBestGenome().getFitness();

                    XmlGenomeWriterStatic.Write(new File(folder + "bestGenome" + j + ".xml"),
                            (NeatGenome) ea.getBestGenome());
                    XmlGenomeWriterStatic.Write(new File(folder + "bestNetwork" + j + ".xml"),
                            SkirmishNetworkEvaluator.substrate.generateMultiGenomeModulus(ea.getBestGenome().decode(null), 5));

                }
                System.out.println(ea.getGeneration() + " " + ea.getBestGenome().getFitness() + " " + (System.currentTimeMillis() - dt));
                //Do any post-hoc stuff here

                sw.println(ea.getGeneration() + " " + (maxFitness));

            }
            sw.close();

            XmlGenomeWriterStatic.Write(new File(folder + "bestGenome.xml"),
                    (NeatGenome) ea.getBestGenome(),
                    ActivationFunctionFactory.getActivationFunction("NullFn")
            );
        }
    }
}
