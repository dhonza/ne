package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.pmatrix.ParameterCombination;
import common.xml.XMLSerialization;
import hyper.cppn.ICPPN;
import hyper.cppn.FakeArrayCPPN;
import hyper.evaluate.IProblem;
import opt.DoubleVectorGenome;
import opt.cmaes.CMAES;
import opt.cmaes.CMAESBasicProgressPrinter;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMAESProgressPrinter1D extends CommonProgressPrinter1D {
    final private CMAES cmaes;

    public CMAESProgressPrinter1D(CMAES cmaes, IProgressPrinter progressPrinter, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        super(progressPrinter, problem, reportStorage, parameters);
        this.cmaes = cmaes;
    }

    public CMAESProgressPrinter1D(CMAES cmaes, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this(cmaes, new CMAESBasicProgressPrinter(cmaes), problem, reportStorage, parameters);
    }

    @Override
    protected ICPPN createBSFCPPN() {
        return new FakeArrayCPPN(cmaes.getMaxReached(), problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        DoubleVectorGenome cppn = new DoubleVectorGenome(cmaes.getMaxReached());
        XMLSerialization.save(cppn, reportStorage.getCompleteFilename(fileName, ".xml"));
    }
}