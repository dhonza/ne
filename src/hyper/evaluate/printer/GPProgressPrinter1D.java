package hyper.evaluate.printer;

import gp.BasicProgressPrinter;
import gp.Forest;
import gp.ForestStorage;
import gp.GP;
import hyper.builder.MathematicaHyperGraphSubstrateBuilder2D;
import hyper.builder.NetSubstrateBuilder;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Substrate;
import neat.Net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPProgressPrinter1D extends BasicProgressPrinter {
    //TODO opakuje se v NetProgressPrinter
    final private Substrate substrate;
    final private Problem problem;

    public GPProgressPrinter1D(GP gp, Substrate substrate, Problem problem) {
        super(gp);
        this.substrate = substrate;
        this.problem = problem;
    }

    @Override
    public void printProgress() {
        CPPN aCPPN = new BasicGPCPPN(gp.getBestSoFar(), substrate.getMaxDimension());
        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
        substrateBuilder.build(aCPPN);
        Net hyperNet = substrateBuilder.getNet();
//        problem.show(hyperNet);
        System.out.println("BSF = " + gp.getBestSoFar());
    }

    @Override
    public void printFinished() {
        System.out.println("FINISHED BSF: " + gp.getBestSoFar());
        Forest forestBSF = gp.getBestSoFar();
        ForestStorage.save(forestBSF, "best.xml");
        CPPN aCPPN = new BasicGPCPPN(forestBSF, substrate.getMaxDimension());
        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
        substrateBuilder.build(aCPPN);
        Net hyperNet = substrateBuilder.getNet();
        System.out.println(hyperNet);
        problem.show(hyperNet);


        System.out.println("trees := " + gp.getBestSoFar().toMathematicaExpression());
        MathematicaHyperGraphSubstrateBuilder2D mathematicaBuilder = new MathematicaHyperGraphSubstrateBuilder2D(substrate);
        mathematicaBuilder.build(aCPPN);
        System.out.println(mathematicaBuilder.getMathematicaExpression());

        System.out.println("hyperNet = " + hyperNet);
    }
}