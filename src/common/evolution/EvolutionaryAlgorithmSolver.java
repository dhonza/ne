package common.evolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 2:59:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvolutionaryAlgorithmSolver {
    final private EvolutionaryAlgorithm evolutionaryAlgorithm;
    final private List<ProgressPrinter> progressPrinterList = new ArrayList<ProgressPrinter>();
    final private List<StopCondition> stopConditionList = new ArrayList<StopCondition>();

    public EvolutionaryAlgorithmSolver(EvolutionaryAlgorithm evolutionaryAlgorithm) {
        this.evolutionaryAlgorithm = evolutionaryAlgorithm;
    }

    public void run() {
        evolutionaryAlgorithm.initialGeneration();

        printGeneration();

        while (!toStop()) {
            evolutionaryAlgorithm.nextGeneration();
            printGeneration();
            printProgress();
        }

        for (ProgressPrinter progressPrinter : progressPrinterList) {
            progressPrinter.printFinished();
        }
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
