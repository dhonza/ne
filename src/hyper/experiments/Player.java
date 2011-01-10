package hyper.experiments;

import common.evolution.IEvaluable;
import common.evolution.IGenotypeToPhenotype;
import common.net.INet;
import common.net.linked.NetStorage;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.xml.XMLSerialization;
import gp.Forest;
import gp.ForestStorage;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.cppn.FakeArrayCPPN;
import hyper.evaluate.ConverterFactory;
import hyper.evaluate.HyperEvaluator;
import hyper.evaluate.IProblem;
import hyper.evaluate.ProblemFactory;
import opt.DoubleVectorGenome;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/7/11
 * Time: 10:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Player {
    static String cfgFile = "cfg/robots/experiment.properties";
    //        String aCPPNFile = "../exp/110109123434_1/bestCPPN_001_001.xml";
    static String aCPPNFile = "bestCPPN_001_001.xml";

    public static void playGP() {
        Forest aCPPN = ForestStorage.load(aCPPNFile);
        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(cfgFile));
        for (ParameterCombination combination : manager) {
            IProblem<INet> problem = ProblemFactory.getProblem(combination, null);
            IEvaluableSubstrateBuilder substrateBuilder =
                    SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), combination);

            IGenotypeToPhenotype<Forest, INet> converter = ConverterFactory.getConverter(combination, substrateBuilder, problem);

            INet net = converter.transform(aCPPN);

            problem.show(net);
        }
    }

    public static void playDirect() {
        DoubleVectorGenome aCPPN = (DoubleVectorGenome) XMLSerialization.load(aCPPNFile);
        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(cfgFile));
        for (ParameterCombination combination : manager) {
            IProblem<INet> problem = ProblemFactory.getProblem(combination, null);
            IEvaluableSubstrateBuilder substrateBuilder =
                    SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), combination);

            IGenotypeToPhenotype<DoubleVectorGenome, INet> converter = ConverterFactory.getConverter(combination, substrateBuilder, problem);

            INet net = converter.transform(aCPPN);

            problem.show(net);
        }
    }

    public static void main(String[] args) {
        playGP();
//        playDirect();
    }
}
