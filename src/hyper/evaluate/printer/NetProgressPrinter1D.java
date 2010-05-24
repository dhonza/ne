package hyper.evaluate.printer;

import common.evolution.ProgressPrinter;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Substrate;
import neat.NEAT;
import neat.NEATBasicProgressPrinter;
import neat.NetStorage;
import neat.Population;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetProgressPrinter1D extends CommonProgressPrinter1D {
    final private NEAT neat;
    final private Population pop;

    public NetProgressPrinter1D(NEAT neat, ProgressPrinter progressPrinter, Substrate substrate, Problem problem) {
        super(progressPrinter, substrate, problem);
        this.neat = neat;
        this.pop = neat.getPopulation();
    }

    public NetProgressPrinter1D(NEAT neat, Substrate substrate, Problem problem) {
        this(neat, new NEATBasicProgressPrinter(neat), substrate, problem);
    }

    @Override
    protected CPPN createBSFCPPN() {
        return new BasicNetCPPN(pop.getBestSoFarNet(), substrate.getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        NetStorage.save(pop.getBestSoFarNet(), fileName);
    }
}
