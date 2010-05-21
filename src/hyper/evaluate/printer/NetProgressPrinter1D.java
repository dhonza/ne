package hyper.evaluate.printer;

import common.function.Function3D;
import hyper.builder.NetSubstrateBuilder;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Coordinate1D;
import hyper.substrate.Substrate;
import neat.BasicProgressPrinter;
import neat.Net;
import neat.Population;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetProgressPrinter1D extends BasicProgressPrinter {
    final private Substrate substrate;
    final private Problem problem;


    class WeightsDraw implements Function3D {
        private CPPN aCPPN;

        WeightsDraw(CPPN aCPPN) {
            this.aCPPN = aCPPN;
        }

        public double getValue(double x, double y) {
            double val = aCPPN.evaluate(0, new Coordinate1D(x), new Coordinate1D(y));
            return val;
        }
    }

    public NetProgressPrinter1D(Population pop, Substrate substrate, Problem problem) {
        super(pop);
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
