package hyper.experiments.reco;

import common.stats.Stats;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 30, 2010
 * Time: 3:58:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportStorage implements Serializable {
    final private static String BASE_DIR_PREFIX = "exp";
    final private static String PARAMETER_FILE_PREFIX = "parameters_";
    final private static String EXPERIMENT_FILE_PREFIX = "experiments_";
    final private static String EXPERIMENTS_OVERALL_FILE_PREFIX = "experiments_overall";
    final private static String SINGLE_RUN_FILE_PREFIX = "run_";
    final private static String SUFFIX = ".txt";

    private File baseDir;
    final private StringBuilder experimentOverallBuilder = new StringBuilder();
    private String generationInfo;

    public ReportStorage(String baseDir) {
        this.baseDir = new File(baseDir);
        this.baseDir.mkdirs();
    }

    public ReportStorage() {
        File baseDirPrefix = new File(BASE_DIR_PREFIX);
        Date time = Calendar.getInstance().getTime();
        String suffix = new SimpleDateFormat("yyMMddHHmmss").format(time);
        int cnt = 1;
        do {
            baseDir = new File(baseDirPrefix, suffix + "_" + cnt++);
        } while (baseDir.isDirectory() || baseDir.isFile());
        if (!baseDir.mkdirs()) {
            throw new IllegalStateException("can not create report directory!");
        }

    }

    public void storeParameters(int parameterCombinationId, String parameterInfo) {
        File file = new File(baseDir, PARAMETER_FILE_PREFIX +
                String.format("%03d", parameterCombinationId) +
                SUFFIX);
        try {
            FileUtils.writeStringToFile(file, parameterInfo);
        } catch (IOException e) {
            System.err.println("Cannot save parameters file: " + file);
        }
    }

    public void storeExperimentResults(int parameterCombinationId, Stats stats) {
        File file = new File(baseDir, EXPERIMENT_FILE_PREFIX +
                String.format("%03d", parameterCombinationId) +
                SUFFIX);
        try {
            FileUtils.writeStringToFile(file, stats.dataToString("EXPERIMENT"));
        } catch (IOException e) {
            System.err.println("Cannot save experiments result file: " + file);
        }
    }

    public void appendExperimentsOverallResults(int parameterCombinationId, String changingParameters, Stats stats) {
        experimentOverallBuilder.append(parameterCombinationId).append(": ").
                append(changingParameters).append('\n').
                append(stats.scopeToString("EXPERIMENT")).append('\n');
    }

    public void storeExperimentsOverallResults() {
        File file = new File(baseDir, EXPERIMENTS_OVERALL_FILE_PREFIX +
                SUFFIX);
        try {
            FileUtils.writeStringToFile(file, experimentOverallBuilder.toString());
        } catch (IOException e) {
            System.err.println("Cannot save experiments result file: " + file);
        }
    }

    public void prepareSingleRunResults(String generationInfo) {
        this.generationInfo = generationInfo;
    }

    public void storeSingleRunResults(int parameterCombinationId, int experimentId) {
        File file = new File(baseDir, SINGLE_RUN_FILE_PREFIX +
                String.format("%03d", parameterCombinationId) + "_" +
                String.format("%03d", experimentId + 1) + SUFFIX);
        try {
            FileUtils.writeStringToFile(file, generationInfo);
        } catch (IOException e) {
            System.err.println("Cannot save single run result file: " + file);
        }
    }
}
