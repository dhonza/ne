package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.cppn.ICPPN;
import hyper.evaluate.IProblem;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class CommonProgressPrinter1D implements IProgressPrinter {
    final private IProgressPrinter progressPrinter;
    final protected IProblem problem;
    final protected ReportStorage reportStorage;
    final protected ParameterCombination parameters;

    protected boolean generation = true;

    protected boolean progress = true;
    protected boolean progressShowCPPN = true;
    protected boolean progressShowHyperNet = true;
    protected boolean progressShowProblem = true;

    protected boolean finished = true;
    protected boolean finishedShowHyperNet = true;
    protected boolean finishedShowProblem = true;

    public CommonProgressPrinter1D(IProgressPrinter progressPrinter, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this.progressPrinter = progressPrinter;
        this.problem = problem;
        this.reportStorage = reportStorage;
        this.parameters = parameters;
        setParameters();
    }

    private void setParameters() {
//        Utils.setParameters(parameters, this, "PRINT"); //this needed public access
        generation = parameters.contains("PRINT.generation") ? parameters.getBoolean("PRINT.generation") : generation;

        progress = parameters.contains("PRINT.progress") ? parameters.getBoolean("PRINT.progress") : progress;
        progressShowCPPN = parameters.contains("PRINT.progressShowCPPN") ? parameters.getBoolean("PRINT.progressShowCPPN") : progressShowCPPN;
        progressShowHyperNet = parameters.contains("PRINT.progressShowHyperNet") ? parameters.getBoolean("PRINT.progressShowHyperNet") : progressShowHyperNet;
        progressShowProblem = parameters.contains("PRINT.progressShowProblem") ? parameters.getBoolean("PRINT.progressShowProblem") : progressShowProblem;

        finished = parameters.contains("PRINT.finished") ? parameters.getBoolean("PRINT.finished") : finished;
        finishedShowHyperNet = parameters.contains("PRINT.finishedShowHyperNet") ? parameters.getBoolean("PRINT.finishedShowHyperNet") : finishedShowHyperNet;
        finishedShowProblem = parameters.contains("PRINT.finishedShowProblem") ? parameters.getBoolean("PRINT.finishedShowProblem") : finishedShowProblem;
    }

    public void printGeneration() {
        if (!generation) {
            return;
        }
        progressPrinter.printGeneration();
    }

    public void printProgress() {
        if (!progress) {
            return;
        }
        progressPrinter.printGeneration();
        progressPrinter.printProgress();

        if (progressShowCPPN) {
            storeBSFCPPN("bestCPPN_");
        }

        showHyperNet(progressShowHyperNet);
        showProblem(progressShowProblem);
    }

    public void printFinished() {
        if (!finished) {
            return;
        }
        progressPrinter.printFinished();
        progressPrinter.printGeneration();
        progressPrinter.printProgress();

        showHyperNet(finishedShowHyperNet);
        showProblem(finishedShowProblem);

        System.out.println("");

        storeBSFCPPN("bestCPPN_");

        /*
        System.out.println("trees := " + gp.getBestSoFar().toMathematicaExpression());
        MathematicaHyperGraphSubstrateBuilder2D mathematicaBuilder = new MathematicaHyperGraphSubstrateBuilder2D(substrate);
        mathematicaBuilder.build(aCPPN);
        System.out.println(mathematicaBuilder.getMathematicaExpression());

        System.out.println("hyperNet = " + hyperNet);
        */
    }

    private void showProblem(boolean show) {
        if (show) {
            ICPPN aCPPN = createBSFCPPN();
            IEvaluableSubstrateBuilder substrateBuilder = createSubstrateBuilder();
            substrateBuilder.build(aCPPN);
            INet hyperNet = createHyperNet(substrateBuilder);
            problem.show(hyperNet);
        }
    }

    private void showHyperNet(boolean show) {
        if (show) {
            ICPPN aCPPN = createBSFCPPN();
            IEvaluableSubstrateBuilder substrateBuilder = createSubstrateBuilder();
//            IEvaluableSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);
            substrateBuilder.build(aCPPN);
            INet hyperNet = createHyperNet(substrateBuilder);
            System.out.println(hyperNet);
        }
    }

    protected abstract ICPPN createBSFCPPN();

    protected IEvaluableSubstrateBuilder createSubstrateBuilder() {
        return SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);
    }

    protected INet createHyperNet(IEvaluableSubstrateBuilder substrateBuilder) {
        return substrateBuilder.getNet();
    }

    protected abstract void storeBSFCPPN(String fileName);
}