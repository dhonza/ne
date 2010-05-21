package hyper.evaluate.printer;

import common.function.Function3D;
import gp.BasicProgressPrinter;
import gp.Forest;
import gp.ForestStorage;
import hyper.builder.MathematicaHyperGraphSubstrateBuilder2D;
import hyper.builder.NetSubstrateBuilder;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Coordinate1D;
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


    class WeightsDraw implements Function3D {
        private CPPN aCPPN;

        WeightsDraw(CPPN aCPPN) {
            this.aCPPN = aCPPN;
        }

        public double getValue(double x, double y) {
            double val = aCPPN.evaluate(1, new Coordinate1D(x), new Coordinate1D(y));
            return val;
        }
    }

    public GPProgressPrinter1D(Substrate substrate, Problem problem) {
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

/*
        JK ex = new JK("HyperGP");
        JKCanvas can;
//        vn = new VNet();
//        vn.drawRoundFrame(500, 500, net);
//        vn.setLocation(510, 0);
//        vn.start();
        double h = 0.5;
        double v = 0.5;
        double H = h * 1.1;
        double V = v * 1.1;
        can = ex.openGraphics(-H, -V, H, V, 400, 400);
        ex.setLocation(0, 300);
        can.setColor(Color.BLUE);
        can.drawGreyMap(1, new WeightsDraw(aCPPN), -h, h, -v, v, -1.0, 1.0);

        while (true) {
            can.repaint();
        }
*/
    }
}