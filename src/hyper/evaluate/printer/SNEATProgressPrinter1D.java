package hyper.evaluate.printer;

import hyper.builder.NetSubstrateBuilder;
import hyper.cppn.BasicSNEATCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Substrate;
import neat.Net;
import sneat.SNEAT;
import sneat.SNEATBasicProgressPrinter;
import sneat.neuralnetwork.INetwork;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATProgressPrinter1D extends SNEATBasicProgressPrinter {
    final private Substrate substrate;
    final private Problem problem;

    public SNEATProgressPrinter1D(SNEAT sneat, Substrate substrate, Problem problem) {
        super(sneat);
        this.substrate = substrate;
        this.problem = problem;
    }

    public void printProgress() {
        INetwork network = sneat.getEA().getBestGenome().decode(null);
        System.out.println("BSF CPPF: " + network);
        CPPN aCPPN = new BasicSNEATCPPN(network, substrate.getMaxDimension());
        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
        substrateBuilder.build(aCPPN);
        Net hyperNet = substrateBuilder.getNet();
        problem.show(hyperNet);
    }

    public void printFinished() {
        INetwork network = sneat.getEA().getBestGenome().decode(null);
        CPPN aCPPN = new BasicSNEATCPPN(network, substrate.getMaxDimension());
        NetSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
        substrateBuilder.build(aCPPN);
        Net hyperNet = substrateBuilder.getNet();
        System.out.println("hyperNet = " + hyperNet);
    }
}