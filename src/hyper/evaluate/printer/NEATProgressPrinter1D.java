package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.pmatrix.ParameterCombination;
import common.xml.XMLSerialization;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.ICPPN;
import hyper.evaluate.IProblem;
import hyper.evaluate.storage.GenomeStorage;
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

    public NEATProgressPrinter1D(NEAT neat, IProgressPrinter progressPrinter, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        super(progressPrinter, problem, reportStorage, parameters);
        this.neat = neat;
        this.pop = neat.getPopulation();
    }

    public NEATProgressPrinter1D(NEAT neat, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this(neat, new NEATBasicProgressPrinter(neat), problem, reportStorage, parameters);
    }

    @Override
    protected ICPPN createBSFCPPN() {
        return new BasicNetCPPN(pop.getBestSoFarNet(), problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        GenomeStorage.saveGenome(parameters, pop.getBestSoFarNet(), reportStorage.getCompleteFilename(fileName, ".xml"));
    }
}
