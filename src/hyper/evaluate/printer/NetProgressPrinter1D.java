package hyper.evaluate.printer;

import hyper.builder.NetSubstrateBuilder;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Substrate;
import neat.NEAT;
import neat.NEATBasicProgressPrinter;
import neat.Net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetProgressPrinter1D extends NEATBasicProgressPrinter {
    final private Substrate substrate;
    final private Problem problem;

    public NetProgressPrinter1D(NEAT neat, Substrate substrate, Problem problem) {
        super(neat);
        this.substrate = substrate;
        this.problem = problem;
    }

    @Override
    public void printProgress() {
        System.out.println("pop.getBestSoFarNet() = " + pop.getBestSoFarNet());
        CPPN aCPPN = new BasicNetCPPN(pop.getBestSoFarNet(), substrate.getMaxDimension());
        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
        substrateBuilder.build(aCPPN);
        Net hyperNet = substrateBuilder.getNet();
        problem.show(hyperNet);
    }

    @Override
    public void printFinished() {
        CPPN aCPPN = new BasicNetCPPN(pop.getBestSoFarNet(), substrate.getMaxDimension());
        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
        substrateBuilder.build(aCPPN);
        Net hyperNet = substrateBuilder.getNet();
        System.out.println("hyperNet = " + hyperNet);
    }
}
