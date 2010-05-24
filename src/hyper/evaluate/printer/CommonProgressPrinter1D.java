package hyper.evaluate.printer;

import common.evolution.ProgressPrinter;
import hyper.builder.NetSubstrateBuilder;
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
abstract public class CommonProgressPrinter1D implements ProgressPrinter {
    final private ProgressPrinter progressPrinter;
    final protected Substrate substrate;
    final protected Problem problem;

    public CommonProgressPrinter1D(ProgressPrinter progressPrinter, Substrate substrate, Problem problem) {
        this.progressPrinter = progressPrinter;
        this.substrate = substrate;
        this.problem = problem;
    }

    public void printGeneration() {
        progressPrinter.printGeneration();
    }

    public void printProgress() {
        progressPrinter.printProgress();
        CPPN aCPPN = createBSFCPPN();
        NetSubstrateBuilder substrateBuilder = createSubstrateBuilder();
        substrateBuilder.build(aCPPN);
        Net hyperNet = createHyperNet(substrateBuilder);
        problem.show(hyperNet);
    }

    public void printFinished() {
        progressPrinter.printFinished();
        storeBSFCPPN("best.xml");
        /*
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
        */
    }

    protected abstract CPPN createBSFCPPN();

    protected NetSubstrateBuilder createSubstrateBuilder() {
        return new NetSubstrateBuilder(substrate);
    }

    protected Net createHyperNet(NetSubstrateBuilder substrateBuilder) {
        return substrateBuilder.getNet();
    }

    protected abstract void storeBSFCPPN(String fileName);
}