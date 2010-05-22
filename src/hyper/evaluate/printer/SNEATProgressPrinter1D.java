package hyper.evaluate.printer;

import common.evolution.ProgressPrinter;
import hyper.evaluate.Problem;
import hyper.substrate.Substrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATProgressPrinter1D implements ProgressPrinter {
    final private Substrate substrate;
    final private Problem problem;

    public SNEATProgressPrinter1D(Substrate substrate, Problem problem) {
        this.substrate = substrate;
        this.problem = problem;
    }

    public void printProgress() {
//        System.out.println("pop.getBestSoFarNet() = " + pop.getBestSoFarNet());
//        CPPN aCPPN = new BasicNetCPPN(pop.getBestSoFarNet(), substrate.getMaxDimension());
//        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
//        substrateBuilder.build(aCPPN);
//        Net hyperNet = substrateBuilder.getNet();
//        problem.show(hyperNet);
    }

    public void printGeneration() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void printFinished() {
//        CPPN aCPPN = new BasicNetCPPN(pop.getBestSoFarNet(), substrate.getMaxDimension());
//        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
//        substrateBuilder.build(aCPPN);
//        Net hyperNet = substrateBuilder.getNet();
//        System.out.println("hyperNet = " + hyperNet);
    }
}