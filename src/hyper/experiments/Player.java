package hyper.experiments;

import common.evolution.IEvaluable;
import common.evolution.IGenotypeToPhenotype;
import common.net.INet;
import common.net.linked.NetStorage;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import gp.Forest;
import gp.ForestStorage;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.evaluate.ConverterFactory;
import hyper.evaluate.HyperEvaluator;
import hyper.evaluate.IProblem;
import hyper.evaluate.ProblemFactory;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/7/11
 * Time: 10:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Player {
    public static void main(String[] args) {
        String cfgFile = "cfg/robots/experiment.properties";
        String aCPPNFile = "../exp/110107225229_1/bestCPPN_001_001.xml";

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
}
