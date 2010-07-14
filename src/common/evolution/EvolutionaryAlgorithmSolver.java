package common.evolution;

import common.Bench;
import common.stats.Stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 2:59:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvolutionaryAlgorithmSolver implements Serializable {
    final private EvolutionaryAlgorithm evolutionaryAlgorithm;
    final private Stats stats;
    final private boolean generalization;
    final private List<ProgressPrinter> progressPrinterList = new ArrayList<ProgressPrinter>();
    final private List<StopCondition> stopConditionList = new ArrayList<StopCondition>();
    private Bench bench;

    public EvolutionaryAlgorithmSolver(EvolutionaryAlgorithm evolutionaryAlgorithm, Stats stats, boolean generalization) {
        this.evolutionaryAlgorithm = evolutionaryAlgorithm;
        this.stats = stats;
        this.generalization = generalization;
        stats.createDoubleStatIfNotExists("GENERATIONS", "EXPERIMENT", "Number of Generations");
        stats.createDoubleStatIfNotExists("EVALUATIONS", "EXPERIMENT", "Number of Evaluations");
        stats.createDoubleStatIfNotExists("MAX_FITNESS", "EXPERIMENT", "Maximum fitness reached");
        stats.createBooleanStatIfNotExists("SUCCESS", "EXPERIMENT", "Problem solved");
        stats.createLongStatIfNotExists("TIME", "EXPERIMENT", "Time to solve");
    }

    public void run() {
        bench = new Bench();
        evolutionaryAlgorithm.initialGeneration();

        if (generalization) {
            evolutionaryAlgorithm.performGeneralizationTest();
        }

        printGeneration();

        while (!toStop()) {
            evolutionaryAlgorithm.nextGeneration();
            if (generalization) {
                evolutionaryAlgorithm.performGeneralizationTest();
            }

            printGeneration();
            printProgress();
        }
        bench.stop();

        for (ProgressPrinter progressPrinter : progressPrinterList) {
            progressPrinter.printFinished();
        }
        storeFinalStats();
    }

    private void storeFinalStats() {
        stats.addSample("GENERATIONS", (double) evolutionaryAlgorithm.getGeneration());
        stats.addSample("EVALUATIONS", (double) evolutionaryAlgorithm.getEvaluations());
        stats.addSample("MAX_FITNESS", evolutionaryAlgorithm.getMaxFitnessReached());
        stats.addSample("SUCCESS", evolutionaryAlgorithm.isSolved());
        stats.addSample("TIME", bench.getLastTimeInterval());
    }

    private void printProgress() {
        if (evolutionaryAlgorithm.hasImproved()) {
            for (ProgressPrinter progressPrinter : progressPrinterList) {
                progressPrinter.printProgress();
            }
        }
    }

    private void printGeneration() {
        for (ProgressPrinter progressPrinter : progressPrinterList) {
            progressPrinter.printGeneration();
        }
    }

    private boolean toStop() {
        for (StopCondition stopCondition : stopConditionList) {
            if (stopCondition.isMet()) {
                return true;
            }
        }
        return false;
    }

    public void addProgressPrinter(ProgressPrinter progressPrinter) {
        progressPrinterList.add(progressPrinter);
    }

    public void removeProgressPrinter(ProgressPrinter progressPrinter) {
        progressPrinterList.remove(progressPrinter);
    }

    public void addStopCondition(StopCondition stopCondition) {
        stopConditionList.add(stopCondition);
    }

    public void removeStopCondition(StopCondition stopCondition) {
        stopConditionList.remove(stopCondition);
    }
}
