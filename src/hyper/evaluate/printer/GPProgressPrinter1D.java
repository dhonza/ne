package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.pmatrix.ParameterCombination;
import gp.Forest;
import gp.ForestStorage;
import gp.GP;
import gp.GPBasicProgressPrinter;
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
public class GPProgressPrinter1D extends CommonProgressPrinter1D {
    final private GP gp;

    public GPProgressPrinter1D(GP gp, IProgressPrinter progressPrinter, IProblem problem, ParameterCombination parameters) {
        super(progressPrinter, problem, parameters);
        this.gp = gp;
    }

    public GPProgressPrinter1D(GP gp, IProblem problem, ParameterCombination parameters) {
        this(gp, new GPBasicProgressPrinter(gp), problem, parameters);
    }

    @Override
    protected ICPPN createBSFCPPN() {
        return new BasicGPCPPN(gp.getBestSoFar(), problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        Forest forestBSF = gp.getBestSoFar();
        ForestStorage.save(forestBSF, fileName);
    }
}