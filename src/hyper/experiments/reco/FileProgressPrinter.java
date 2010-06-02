package hyper.experiments.reco;

import common.evolution.EvolutionaryAlgorithm;
import common.evolution.ProgressPrinter;

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
        double[] fitnessVector;
    }

    final private EvolutionaryAlgorithm ea;
    final private ReportStorage reportStorage;
    final private List<InfoContainer> generations = new ArrayList<InfoContainer>();

    public FileProgressPrinter(EvolutionaryAlgorithm ea, ReportStorage reportStorage) {
        this.ea = ea;
        this.reportStorage = reportStorage;
    }

    public void printGeneration() {
        InfoContainer container = new InfoContainer();
        container.bsf = ea.getMaxFitnessReached();
        container.fitnessVector = ea.getFitnessVector();
        generations.add(container);
    }

    public void printProgress() {
    }

    public void printFinished() {
        StringBuilder builder = new StringBuilder();
        int last = generations.get(0).fitnessVector.length - 1;

        //header
        builder.append("BSF").append("\t");
        for (int i = 0; i < last; i++) {
            builder.append(i + 1).append("\t");
        }
        builder.append(last + 1).append("\n");

        //data
        for (InfoContainer generation : generations) {
            builder.append(generation.bsf).append("\t");
            for (int i = 0; i < last; i++) {
                builder.append(generation.fitnessVector[i]).append("\t");
            }
            builder.append(generation.fitnessVector[last]).append("\n");
        }
        reportStorage.prepareSingleRunResults(builder.toString());
    }
}
