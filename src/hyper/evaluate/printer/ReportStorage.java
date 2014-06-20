package hyper.evaluate.printer;

import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.stats.Stats;
import org.apache.commons.io.FileUtils;

import java.io.*;
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

    final private static String BASE_DIR_PREFIX = "../exp";
    final private static String PARAMETER_FILE_PREFIX = "parameters_";
    final private static String EXPERIMENT_FILE_PREFIX = "experiments_";
    final private static String EXPERIMENTS_OVERALL_FILE_PREFIX = "experiments_overall";
    final private static String SINGLE_RUN_FILE_PREFIX = "run_";
    final private static String SUFFIX = ".txt";
    final private static String SUFFIX_TEMP = ".tmp.txt";
    final private static String TOTAL_FILE = "_total.txt";
    final private static String SEED_FILE = "_seed.txt";

    private File baseDir;
    private BufferedWriter experimentOverallFile;
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
            throw new IllegalStateException("Cannot create report directory: " + baseDir + "!");
        }

    }

    public void incrementExperimentId() {
        experimentId++;
    }

    public void prepareNewParameterCombination() {
        parameterCombinationId++;
        experimentId = 1;
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

    public void openExperimentsOverallResults() {
        try {
            experimentOverallFile = new BufferedWriter(new FileWriter(new File(baseDir, EXPERIMENTS_OVERALL_FILE_PREFIX + SUFFIX_TEMP)));
        } catch (IOException e) {
            System.err.println("Cannot open experiments result file: " + experimentOverallFile);
            System.exit(1);
        }
    }

    public void appendExperimentsOverallResults(String changingParameters, Stats stats) {
        StringBuilder experimentOverallBuilder = new StringBuilder();
        experimentOverallBuilder.append(parameterCombinationId).append(": ").
                append(changingParameters).append('\n').
                append(stats.scopeToString("EXPERIMENT")).append('\n');
        try {
            experimentOverallFile.append(experimentOverallBuilder.toString());
            experimentOverallFile.flush();
        } catch (IOException e) {
            System.err.println("Cannot append experiments result file: " + experimentOverallFile);
            System.exit(1);
        }
    }

    public void closeExperimentsOverallResults() {
        //originally the file was written at once at the end
        //any final strings can be appended here
        try {
            FileWriter tw = new FileWriter(new File(baseDir, TOTAL_FILE), true);
            BufferedWriter totalWriter = new BufferedWriter(tw);
            totalWriter.write("# END\n");
            totalWriter.write(new Date().toString() + "\n");
            totalWriter.close();
            experimentOverallFile.close();
            new File(baseDir, EXPERIMENTS_OVERALL_FILE_PREFIX + SUFFIX_TEMP).renameTo(new File(baseDir, EXPERIMENTS_OVERALL_FILE_PREFIX + SUFFIX));
        } catch (IOException e) {
            System.err.println("Cannot close experiments result file: " + experimentOverallFile);
        }
    }

    public void prepareSingleRunResults(List<SingleRunFile> generationInfo) {
        this.generationInfo.clear();
        this.generationInfo.addAll(generationInfo);
    }

    public void startAll(long seed, ParameterMatrixManager manager) {
        File experimentIdFile = new File(baseDir, SEED_FILE);
        File totalFile = new File(baseDir, TOTAL_FILE);
        try {
            FileUtils.writeStringToFile(experimentIdFile, String.valueOf(seed));
            StringBuilder builder = new StringBuilder();
            builder.append("# START\n");
            builder.append(new Date().toString()).append('\n');
            builder.append("# Number of experiments for each parameter combination\n");
            for (ParameterCombination combination : manager) {
                builder.append(combination.getInteger("EXPERIMENTS")).append('\n');
            }
            FileUtils.writeStringToFile(totalFile, builder.toString());
        } catch (IOException e) {
            System.err.println("Cannot save experiment id file: " + experimentIdFile);
        }
    }

    public void startSingleRun() {
        File experimentIdFile = new File(baseDir, "_experiment_id.txt");
        try {
            FileUtils.writeStringToFile(experimentIdFile, String.valueOf(experimentId));
        } catch (IOException e) {
            System.err.println("Cannot save experiment id file: " + experimentIdFile);
        }
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

    public File getSubDir(String subDirPrefix) {
        return new File(baseDir, subDirPrefix +
                String.format("%03d", parameterCombinationId) + "_" +
                String.format("%03d", experimentId));
    }


    public String getCompleteFilename(String prefix, String suffix) {
        //TODO this code is copy-pasted through this class
        return (new File(baseDir, prefix +
                String.format("%03d", parameterCombinationId) + "_" +
                String.format("%03d", experimentId) + suffix)).toString();
    }
}
