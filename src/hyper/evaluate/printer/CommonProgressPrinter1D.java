package hyper.evaluate.printer;

import common.evolution.ProgressPrinter;
import common.pmatrix.ParameterCombination;
import hyper.builder.NetSubstrateBuilder;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Substrate;
import common.net.INet;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class CommonProgressPrinter1D implements ProgressPrinter {
    final private ProgressPrinter progressPrinter;
    final protected Substrate substrate;
    final protected Problem problem;
    final protected ParameterCombination parameters;

    protected boolean generation = true;

    protected boolean progress = true;
    protected boolean progressShowProblem = true;

    protected boolean finished = true;
    protected boolean finishedShowProblem = true;

    public CommonProgressPrinter1D(ProgressPrinter progressPrinter, Substrate substrate, Problem problem, ParameterCombination parameters) {
        this.progressPrinter = progressPrinter;
        this.substrate = substrate;
        this.problem = problem;
        this.parameters = parameters;
        setParameters();
    }

    private void setParameters() {
//        Utils.setParameters(parameters, this, "PRINT"); //this needed public access
        generation = parameters.contains("PRINT.generation") ? parameters.getBoolean("PRINT.generation") : generation;

        progress = parameters.contains("PRINT.progress") ? parameters.getBoolean("PRINT.progress") : progress;
        progressShowProblem = parameters.contains("PRINT.progressShowProblem") ? parameters.getBoolean("PRINT.progressShowProblem") : progressShowProblem;

        finished = parameters.contains("PRINT.finished") ? parameters.getBoolean("PRINT.finished") : finished;
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

        showProblem(progressShowProblem);
    }

    public void printFinished() {
        if (!finished) {
            return;
        }
        progressPrinter.printFinished();
        progressPrinter.printGeneration();
        progressPrinter.printProgress();

        showProblem(finishedShowProblem);

        System.out.println("");

        storeBSFCPPN("best.xml");

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
            CPPN aCPPN = createBSFCPPN();
            NetSubstrateBuilder substrateBuilder = createSubstrateBuilder();
            substrateBuilder.build(aCPPN);
            INet hyperNet = createHyperNet(substrateBuilder);
            problem.show(hyperNet);
        }
    }

    protected abstract CPPN createBSFCPPN();

    protected NetSubstrateBuilder createSubstrateBuilder() {
        return new NetSubstrateBuilder(substrate);
    }

    protected INet createHyperNet(NetSubstrateBuilder substrateBuilder) {
        return substrateBuilder.getNet();
    }

    protected abstract void storeBSFCPPN(String fileName);
}