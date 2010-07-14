package hyper.evaluate.printer;

import common.evolution.ProgressPrinter;
import common.pmatrix.ParameterCombination;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.IProblem;
import neat.NEAT;
import neat.NEATBasicProgressPrinter;
import common.net.linked.NetStorage;
import neat.Population;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class NEATProgressPrinter1D extends CommonProgressPrinter1D {
    final private NEAT neat;
    final private Population pop;

    public NEATProgressPrinter1D(NEAT neat, ProgressPrinter progressPrinter, IProblem problem, ParameterCombination parameters) {
        super(progressPrinter, problem, parameters);
        this.neat = neat;
        this.pop = neat.getPopulation();
    }

    public NEATProgressPrinter1D(NEAT neat, IProblem problem, ParameterCombination parameters) {
        this(neat, new NEATBasicProgressPrinter(neat), problem, parameters);
    }

    @Override
    protected CPPN createBSFCPPN() {
        return new BasicNetCPPN(pop.getBestSoFarNet(), problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        NetStorage.save(pop.getBestSoFarNet(), fileName);
    }
}
