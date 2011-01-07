package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.pmatrix.ParameterCombination;
import hyper.cppn.ICPPN;
import hyper.cppn.FakeArrayCPPN;
import hyper.evaluate.IProblem;
import opt.sade.SADE;
import opt.sade.SADEBasicProgressPrinter;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SADEProgressPrinter1D extends CommonProgressPrinter1D {
    final private SADE sade;

    public SADEProgressPrinter1D(SADE sade, IProgressPrinter progressPrinter, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        super(progressPrinter, problem, reportStorage, parameters);
        this.sade = sade;
    }

    public SADEProgressPrinter1D(SADE sade, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this(sade, new SADEBasicProgressPrinter(sade), problem, reportStorage, parameters);
    }

    @Override
    protected ICPPN createBSFCPPN() {
        return new FakeArrayCPPN(sade.getBsf(), problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        System.out.println("NO storeBSFCPPN for SADE!!!!");
    }
}