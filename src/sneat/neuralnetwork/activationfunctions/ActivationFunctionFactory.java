package sneat.neuralnetwork.activationfunctions;

import common.pmatrix.Utils;
import sneat.evolution.NeatParameters;
import sneat.maths.RouletteWheel;
import sneat.neuralnetwork.IActivationFunction;

import java.util.HashMap;
import java.util.Map;

public class ActivationFunctionFactory {
    public static double[] probabilities;
    public static IActivationFunction[] functions;


    public static void setSameProbabilitiesForList(String listOfFunctions) {
        String[] classNames = Utils.extractIdentificators(listOfFunctions);
        Map<String, Double> probs = new HashMap<String, Double>();
        double prob = 1.0 / classNames.length;
        for (String className : classNames) {
            probs.put(className, prob);
        }
        setProbabilities(probs);
    }

    public static void setProbabilities(Map<String, Double> probs) {
        probabilities = new double[probs.size()];
        functions = new IActivationFunction[probs.size()];
        int counter = 0;
        for (Map.Entry<String, Double> funct : probs.entrySet()) {
            probabilities[counter] = funct.getValue();
            functions[counter] = getActivationFunction(funct.getKey());
            counter++;
        }

    }

    public static Map<String, IActivationFunction> activationFunctionTable = new HashMap<String, IActivationFunction>();

    public static IActivationFunction getActivationFunction(String functionId) {
        IActivationFunction activationFunction = ActivationFunctionFactory.activationFunctionTable.get(functionId);
        if (activationFunction == null) {
            activationFunction = createActivationFunction(functionId);
            activationFunctionTable.put(functionId, activationFunction);
        }
        return activationFunction;
    }

    private static IActivationFunction createActivationFunction(String functionId) {
        // For now the function ID is the name of a class that implements IActivationFunction.
        String className = ActivationFunctionFactory.class.getPackage().getName() + '.' + functionId;

        try {
            Class classDefinition = Class.forName(className);
            return (IActivationFunction) classDefinition.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
//        return (IActivationFunction) Assembly.GetExecutingAssembly().CreateInstance(className);
        return null;
    }

    public static IActivationFunction getRandomActivationFunction(NeatParameters np) {
        return functions[RouletteWheel.singleThrow(probabilities)];
    }
}