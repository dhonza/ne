package common.evolution;

import hyper.evaluate.converter.DirectGenomeToINet;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 19, 2010
 * Time: 7:28:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class PopulationManager<G, P> {
    private class ThreadEvaluator implements Runnable {
        final private CountDownLatch stopLatch;
        final private IEvaluable<P> evaluator;
        final private List<G> part;
        final private EvaluationInfo[] evaluationInfo;

        private ThreadEvaluator(CountDownLatch stopLatch, IEvaluable<P> evaluator, List<G> part) {
            this.evaluator = evaluator;
            this.part = part;
            this.stopLatch = stopLatch;
            this.evaluationInfo = new EvaluationInfo[part.size()];
        }

        public void run() {
            for (int i = 0; i < part.size(); i++) {
                evaluationInfo[i] = evaluator.evaluate(populationStorage.getPhenome(i));
            }
            stopLatch.countDown();
        }

        public EvaluationInfo[] getEvaluationInfo() {
            return evaluationInfo;
        }
    }

    final private SimplePopulationStorage<G, P> populationStorage;
    final private PopulationEvaluator<P> populationEvaluator;
    final private List<IGenotypeToPhenotype<G, P>> perThreadConverters;
    final private List<IEvaluable<P>> perThreadEvaluators;
    final private ExecutorService threadExecutor = Executors.newCachedThreadPool();

    private int threads;
    private int minEvalsPerThread;
    private int remainingEvals;

    public PopulationManager(List<IGenotypeToPhenotype<G, P>> perThreadConverters, List<IEvaluable<P>> perThreadEvaluators) {
        this.perThreadConverters = perThreadConverters;
        this.perThreadEvaluators = perThreadEvaluators;
        populationStorage = new SimplePopulationStorage<G, P>(threadExecutor, perThreadConverters);
        populationEvaluator = new PopulationEvaluator<P>(threadExecutor, perThreadEvaluators, populationStorage);
    }

    public void loadGenotypes(List<G> population) {
        populationStorage.loadGenomes(population);
    }

    public List<EvaluationInfo> evaluate() {
        populationStorage.convert();
//        checkAgainstSequential(evaluationInfo);        
        return populationEvaluator.evaluate();
    }

    public EvaluationInfo evaluateGeneralization(G individual) {
        return perThreadEvaluators.get(0).evaluateGeneralization(perThreadConverters.get(0).transform(individual));
    }

    private void checkAgainstSequential(EvaluationInfo[] evaluationInfos) {
        //checking parallel vs. sequential
        EvaluationInfo[] evaluationInfos2 = new EvaluationInfo[populationStorage.getPhenomes().size()];
        for (int i = 0; i < populationStorage.getPhenomes().size(); i++) {
            evaluationInfos2[i] = perThreadEvaluators.get(0).evaluate(perThreadConverters.get(0).transform(populationStorage.getGenome(i)));
        }

        for (int i = 0; i < evaluationInfos.length; i++) {
            if (evaluationInfos[i].getFitness() != evaluationInfos2[i].getFitness()) {
                System.out.println("DIFFERENT");
                System.out.println(ArrayUtils.toString(evaluationInfos));
                System.out.println(ArrayUtils.toString(evaluationInfos2));
                break;
            }
        }
    }

    public static int getNumberOfThreads() {
        return Runtime.getRuntime().availableProcessors();
    }

    public boolean isSolved() {
        for (IEvaluable<P> evaluator : perThreadEvaluators) {
            if (evaluator.isSolved()) {
                return true;
            }
        }
        return false;
    }

    public int getNumberOfInputs() {
        return perThreadEvaluators.get(0).getNumberOfInputs();
    }

    public int getNumberOfOutputs() {
        return perThreadEvaluators.get(0).getNumberOfOutputs();
    }

    /**
     * For direct methods only.
     *
     * @return
     */
    public int getPhenotypeDimension() {
        if (perThreadConverters.get(0) instanceof DirectGenomeToINet) {
            return ((DirectGenomeToINet) perThreadConverters.get(0)).getNumOfLinks();
        } else {
            throw new IllegalStateException("getPhenotypeDimension() meant only for direct methods");
        }
    }

    public void shutdown() {
        threadExecutor.shutdownNow();
    }
}
