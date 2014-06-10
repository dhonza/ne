package hyper.experiments;

import common.RND;
import common.evolution.IGenotypeToPhenotype;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.evaluate.ConverterFactory;
import hyper.evaluate.IProblem;
import hyper.evaluate.ProblemFactory;
import hyper.evaluate.storage.GenomeStorage;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/7/11
 * Time: 10:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Player {
    static String cfgFile = "cfg/hyper/gphyper.properties";
    static String aCPPNFile = "../exp/ALE/bestCPPN_G69_001_001.xml";
//    static String aCPPNFile = "bestCPPN_001_001.xml";

    public static void play() {
        RND.initialize();
        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(cfgFile));
        for (ParameterCombination combination : manager) {
            Object genome = GenomeStorage.loadGenome(combination, aCPPNFile);
            System.out.println(genome);

            IProblem<INet> problem = ProblemFactory.getProblem(combination, null);
            IEvaluableSubstrateBuilder substrateBuilder =
                    SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), combination);

            IGenotypeToPhenotype<Object, INet> converter = ConverterFactory.getConverter(combination, substrateBuilder, problem);

            INet net = converter.transform(genome);
//            System.out.println(net);
//            MathematicaUtils.printMatrixMathematica(((Net) net).toWeightMatrix());

            problem.show(net);
        }
    }

    public static void main(String[] args) {
        play();
    }
}
