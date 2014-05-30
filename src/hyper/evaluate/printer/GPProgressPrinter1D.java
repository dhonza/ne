package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.pmatrix.ParameterCombination;
import gp.GPBase;
import gp.GPBasicProgressPrinter;
import gp.IGPForest;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.ICPPN;
import hyper.evaluate.IProblem;
import hyper.evaluate.storage.GenomeStorage;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPProgressPrinter1D extends CommonProgressPrinter1D {
    final private GPBase gp;

    public GPProgressPrinter1D(GPBase gp, IProgressPrinter progressPrinter, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        super(progressPrinter, problem, reportStorage, parameters);
        this.gp = gp;
    }

    public GPProgressPrinter1D(GPBase gp, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this(gp, new GPBasicProgressPrinter(gp), problem, reportStorage, parameters);
    }

    @Override
    protected ICPPN createBSFCPPN() {
        return new BasicGPCPPN(gp.getBestSoFar(), problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        IGPForest forestBSF = gp.getBestSoFar();
        GenomeStorage.saveGenome(parameters, forestBSF, reportStorage.getCompleteFilename(fileName + "G" + gp.getGeneration() + "_", ".xml"));
    }
}