package hyper.experiments.reco;

import common.stats.Stats;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 30, 2010
 * Time: 3:58:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportStorage implements Serializable {
    public static class SingleRunFile {
        public String name;
        public String text;

        public SingleRunFile(String name, String text) {
            this.name = name;
            this.text = text;
        }
    }

    final private static String BASE_DIR_PREFIX = "exp";
    final private static String PARAMETER_FILE_PREFIX = "parameters_";
    final private static String EXPERIMENT_FILE_PREFIX = "experiments_";
    final private static String EXPERIMENTS_OVERALL_FILE_PREFIX = "experiments_overall";
    final private static String SINGLE_RUN_FILE_PREFIX = "run_";
    final private static String SUFFIX = ".txt";

    private File baseDir;
    final private StringBuilder experimentOverallBuilder = new StringBuilder();
    private List<SingleRunFile> generationInfo = new ArrayList<SingleRunFile>();
    private int experimentId = 1;
    private int parameterCombinationId = 1;

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

    public void incrementExperimentId() {
        experimentId++;
    }

    public void incrementParameterCombinationId() {
        parameterCombinationId++;
    }

    public void storeParameters(String parameterInfo) {
        File file = new File(baseDir, PARAMETER_FILE_PREFIX +
                String.format("%03d", parameterCombinationId) +
                SUFFIX);
        try {
            FileUtils.writeStringToFile(file, parameterInfo);
        } catch (IOException e) {
            System.err.println("Cannot save parameters file: " + file);
        }
    }

    public void storeExperimentResults(Stats stats) {
        File file = new File(baseDir, EXPERIMENT_FILE_PREFIX +
                String.format("%03d", parameterCombinationId) +
                SUFFIX);
        try {
            FileUtils.writeStringToFile(file, stats.dataToString("EXPERIMENT"));
        } catch (IOException e) {
            System.err.println("Cannot save experiments result file: " + file);
        }
    }

    public void appendExperimentsOverallResults(String changingParameters, Stats stats) {
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

    public void prepareSingleRunResults(List<SingleRunFile> generationInfo) {
        this.generationInfo.clear();
        this.generationInfo.addAll(generationInfo);
    }

    public void storeSingleRunResults() {
        for (SingleRunFile singleRunFile : generationInfo) {

            File file = new File(baseDir, SINGLE_RUN_FILE_PREFIX +
                    String.format("%03d", parameterCombinationId) + "_" +
                    String.format("%03d", experimentId) + "_" + singleRunFile.name + SUFFIX);
            try {
                FileUtils.writeStringToFile(file, singleRunFile.text);
            } catch (IOException e) {
                System.err.println("Cannot save single run result file: " + file);
            }
        }
    }

    public void storeSingleRunAuxiliaryFile(String fileNamePrefix, String text) {
        File file = new File(baseDir, fileNamePrefix +
                String.format("%03d", parameterCombinationId) + "_" +
                String.format("%03d", experimentId) + SUFFIX);
        try {
            FileUtils.writeStringToFile(file, text);
        } catch (IOException e) {
            System.err.println("Cannot save single run auxiliary file: " + file);
        }
    }
}
