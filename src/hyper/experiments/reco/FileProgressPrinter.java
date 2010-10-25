package hyper.experiments.reco;

import common.evolution.BasicInfo;
import common.evolution.EvaluationInfo;
import common.evolution.IEvolutionaryAlgorithm;
import common.evolution.IProgressPrinter;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.IProblem;
import hyper.evaluate.IProblemGeneralization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 2, 2010
 * Time: 4:40:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileProgressPrinter implements IProgressPrinter {
    private static class InfoContainer {
        double bsf;
        List<EvaluationInfo> evaluationInfos;
        EvaluationInfo generalizationInfo;
        BasicInfo populationInfo;
    }

    final private IEvolutionaryAlgorithm ea;
    final private IProblem<INet> problem;
    final private ReportStorage reportStorage;
    final private ParameterCombination parameters;
    private boolean storeRun;

    final private List<InfoContainer> generations = new ArrayList<InfoContainer>();
    final private Set<String> generationsKeys = new HashSet<String>();

    public FileProgressPrinter(IEvolutionaryAlgorithm ea, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
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
        if (problem instanceof IProblemGeneralization) {
            container.generalizationInfo = ea.getGeneralizationEvaluationInfo();
        }
        container.populationInfo = ea.getPopulationInfo();
        generationsKeys.addAll(container.populationInfo.getKeys());
        generations.add(container);
    }

    public void printProgress() {
    }

    public void printFinished() {
        if (!storeRun) {
            return;
        }
        List<ReportStorage.SingleRunFile> itemList = new ArrayList<ReportStorage.SingleRunFile>();
        StringBuilder builder = extractFitnessInfo();
        itemList.add(new ReportStorage.SingleRunFile("FITNESS", builder.toString()));
        for (String name : problem.getEvaluationInfoItemNames()) {
            itemList.add(new ReportStorage.SingleRunFile(name, extractEvaluationInfo(name).toString()));
        }
        if (problem instanceof IProblemGeneralization) {
            itemList.add(new ReportStorage.SingleRunFile("GENERALIZATION_FITNESS", extractGeneralizationFitnessInfo().toString()));
            for (String name : problem.getEvaluationInfoItemNames()) {
                itemList.add(new ReportStorage.SingleRunFile("GENERALIZATION_" + name, extractGeneralizationEvaluationInfo(name).toString()));
            }
        }
        for (String name : new String[]{"G_DIVERSITY", "P_DIVERSITY"}) {
            if (generationsKeys != null) {
                itemList.add(new ReportStorage.SingleRunFile(name, extractPopulationInfo(name).toString()));
            }
        }

        //extract origins
        for (String name : generationsKeys) {
            if(name.startsWith("O_")) {
                itemList.add(new ReportStorage.SingleRunFile(name, extractPopulationInfo(name, 0L).toString()));
            }            
        }

        reportStorage.prepareSingleRunResults(itemList);
    }

    private StringBuilder extractFitnessInfo() {
        StringBuilder builder = new StringBuilder();
        int last = generations.get(0).evaluationInfos.size() - 1;

        //header
        builder.append("BSF").append("\t");
        for (int i = 0; i < last; i++) {
            builder.append(i + 1).append("\t");
        }
        builder.append(last + 1).append("\n");

        //data
        for (InfoContainer generation : generations) {
            builder.append(generation.bsf).append("\t");
            for (int i = 0; i < generation.evaluationInfos.size() - 1; i++) {
                builder.append(generation.evaluationInfos.get(i).getFitness()).append("\t");
            }
            builder.append(generation.evaluationInfos.get(generation.evaluationInfos.size() - 1).getFitness()).append("\n");
        }
        return builder;
    }

    private StringBuilder extractEvaluationInfo(String name) {
        StringBuilder builder = new StringBuilder();
        int last = generations.get(0).evaluationInfos.size() - 1;

        //header
        for (int i = 0; i < last; i++) {
            builder.append(i + 1).append("\t");
        }
        builder.append(last + 1).append("\n");

        //data
        for (InfoContainer generation : generations) {
            for (int i = 0; i < generation.evaluationInfos.size() - 1; i++) {
                builder.append(generation.evaluationInfos.get(i).getInfo(name)).append("\t");
            }
            builder.append(generation.evaluationInfos.get(generation.evaluationInfos.size() - 1).getInfo(name)).append("\n");
        }
        return builder;
    }

    private StringBuilder extractGeneralizationFitnessInfo() {
        StringBuilder builder = new StringBuilder();
        for (InfoContainer generation : generations) {
            builder.append(generation.generalizationInfo.getFitness()).append("\n");
        }
        return builder;
    }

    private StringBuilder extractGeneralizationEvaluationInfo(String name) {
        StringBuilder builder = new StringBuilder();
        for (InfoContainer generation : generations) {
            builder.append(generation.generalizationInfo.getInfo(name)).append("\n");
        }
        return builder;
    }

    private StringBuilder extractPopulationInfo(String name) {
        return extractPopulationInfo(name, null);
    }

    private StringBuilder extractPopulationInfo(String name, Object defaultValue) {
        StringBuilder builder = new StringBuilder();
        for (InfoContainer generation : generations) {
            Object value = generation.populationInfo.getInfo(name);
            if(value == null) {
                value = defaultValue;
            }
            builder.append(value).append("\n");
        }
        return builder;
    }
}
