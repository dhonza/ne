package hyper.experiments.reco;

import common.evolution.EvaluationInfo;
import common.evolution.EvolutionaryAlgorithm;
import common.evolution.ProgressPrinter;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.Problem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 2, 2010
 * Time: 4:40:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileProgressPrinter implements ProgressPrinter {
    private class InfoContainer {
        double bsf;
        EvaluationInfo[] evaluationInfos;
    }

    final private EvolutionaryAlgorithm ea;
    final private Problem problem;
    final private ReportStorage reportStorage;
    final private ParameterCombination parameters;
    private boolean storeRun;

    final private List<InfoContainer> generations = new ArrayList<InfoContainer>();

    public FileProgressPrinter(EvolutionaryAlgorithm ea, Problem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this.ea = ea;
        this.problem = problem;
        this.reportStorage = reportStorage;
        this.parameters = parameters;
        storeRun = parameters.contains("PRINT.storeRun") ? parameters.getBoolean("PRINT.generation") : storeRun;
    }

    public void printGeneration() {
        if (!storeRun) {
            return;
        }
        InfoContainer container = new InfoContainer();
        container.bsf = ea.getMaxFitnessReached();
        container.evaluationInfos = ea.getEvaluationInfo();
        generations.add(container);
    }

    public void printProgress() {
    }

    public void printFinished() {
        if (!storeRun) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        int last = generations.get(0).evaluationInfos.length - 1;

        //header
        builder.append("BSF").append("\t");
        for (int i = 0; i < last; i++) {
            builder.append(i + 1).append("\t");
        }
        builder.append(last + 1).append("\n");

        //data
        for (InfoContainer generation : generations) {
            builder.append(generation.bsf).append("\t");
            for (int i = 0; i < generation.evaluationInfos.length - 1; i++) {
                builder.append(generation.evaluationInfos[i].getFitness()).append("\t");
            }
            builder.append(generation.evaluationInfos[generation.evaluationInfos.length - 1]).append("\n");
        }
        reportStorage.prepareSingleRunResults(builder.toString());
    }
}
