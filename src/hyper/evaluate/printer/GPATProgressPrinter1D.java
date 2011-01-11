package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.pmatrix.ParameterCombination;
import common.xml.XMLSerialization;
import gp.GPBasicProgressPrinter;
import gp.IGPForest;
import gpat.GPAT;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.ICPPN;
import hyper.evaluate.IProblem;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPATProgressPrinter1D extends CommonProgressPrinter1D {
    final private GPAT gp;

    public GPATProgressPrinter1D(GPAT gp, IProgressPrinter progressPrinter, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        super(progressPrinter, problem, reportStorage, parameters);
        this.gp = gp;
    }

    public GPATProgressPrinter1D(GPAT gp, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this(gp, new GPBasicProgressPrinter(gp), problem, reportStorage, parameters);
    }

    @Override
    protected ICPPN createBSFCPPN() {
        return new BasicGPCPPN(gp.getBestSoFar(), problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        IGPForest forestBSF = gp.getBestSoFar();
        XMLSerialization.save(forestBSF, reportStorage.getCompleteFilename(fileName, ".xml"));
    }
}