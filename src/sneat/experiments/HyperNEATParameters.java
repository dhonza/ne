package sneat.experiments;

import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class HyperNEATParameters {
    public static double threshold = 0;
    public static double weightRange = 0;
    public static int numThreads = 0;
    public static IActivationFunction substrateActivationFunction = null;
    public static Map<String, Double> activationFunctions = new HashMap<String, Double>();
    public static Map<String, String> parameters = new HashMap<String, String>();


    public static void loadParameterFile() {
        System.out.println("Loading SNEAT config file.");
        try {
            BufferedReader input = new BufferedReader(new FileReader("cfg/sneat-params.txt"));
            String wholeLine;
            String[] line;
            double probability;
            boolean readingActivation = false;
            while ((wholeLine = input.readLine()) != null) {
                line = wholeLine.split(" ");
                if (line[0].equals("StartActivationFunctions")) {
                    readingActivation = true;
                } else if (line[0].equals("EndActivationFunctions")) {
                    readingActivation = false;
                } else {
                    if (readingActivation) {
                        probability = Double.valueOf(line[1]);
                        activationFunctions.put(line[0], probability);
                    } else {
                        parameters.put(line[0].toLowerCase(), line[1]);
                    }
                }
            }
            input.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Error reading config file, check file location and formation");
            //close program
        }
        ActivationFunctionFactory.setProbabilities(activationFunctions);

        threshold = setParameterDouble("threshold");
        weightRange = setParameterDouble("weightrange");
        numThreads = setParameterInt("numberofthreads");
        setSubstrateActivationFunction();
    }

    private static void setSubstrateActivationFunction() {
        String parameter = getParameter("substrateactivationfunction");
        if (parameter != null)
            substrateActivationFunction = ActivationFunctionFactory.getActivationFunction(parameter);
    }

    public static String getParameter(String parameter) {
        if (parameters.containsKey(parameter))
            return parameters.get(parameter);
        else
            return null;
    }

    public static double setParameterDouble(String parameter) {
        parameter = getParameter(parameter.toLowerCase());
        if (parameter != null)
            return Double.valueOf(parameter);
        throw new IllegalStateException("Parameter not found");
    }

    public static int setParameterInt(String parameter) {
        parameter = getParameter(parameter.toLowerCase());
        if (parameter != null)
            return Integer.valueOf(parameter);
        throw new IllegalStateException("Parameter not found");
    }
}
