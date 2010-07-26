package common.evolution;

import hyper.evaluate.converter.DirectGenomeToINet;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
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
public class ParallelPopulationEvaluator<G, P> {
    private class ThreadEvaluator implements Runnable {
        final private CountDownLatch stopLatch;
        final private IGenotypeToPhenotype<G, P> converter;
        final private IEvaluable<P> evaluator;
        final private List<G> part;
        final private EvaluationInfo[] evaluationInfo;

        private ThreadEvaluator(CountDownLatch stopLatch, IGenotypeToPhenotype<G, P> converter, IEvaluable<P> evaluator, List<G> part) {
            this.converter = converter;
            this.evaluator = evaluator;
            this.part = part;
            this.stopLatch = stopLatch;
            this.evaluationInfo = new EvaluationInfo[part.size()];
        }

        public void run() {
            for (int i = 0; i < part.size(); i++) {
                P phenotype = converter.convert(part.get(i));
                evaluationInfo[i] = evaluator.evaluate(phenotype);
            }
            stopLatch.countDown();
        }

        public EvaluationInfo[] getEvaluationInfo() {
            return evaluationInfo;
        }
    }

    final private IGenotypeToPhenotype<G, P>[] perThreadConverters;
    final private IEvaluable<P>[] perThreadEvaluators;
    final private ExecutorService threadExecutor = Executors.newCachedThreadPool();

    public ParallelPopulationEvaluator(IGenotypeToPhenotype<G, P>[] perThreadConverters, IEvaluable<P>[] perThreadEvaluators) {
        this.perThreadConverters = perThreadConverters;
        this.perThreadEvaluators = perThreadEvaluators;
    }

    public EvaluationInfo[] evaluate(List<G> population) {
        //sequential run
        if (perThreadEvaluators.length == 1) {
            return sequentialEvaluate(population);
        }

        int threads = ParallelPopulationEvaluator.getNumberOfThreads();
        if (threads > population.size()) {
            threads = population.size();
        }

//        System.out.println("Using threads:" + threads);
        int minEvalsPerThread = population.size() / threads;
        int remainingEvals = population.size() % threads;

        CountDownLatch stopLatch = new CountDownLatch(threads);
        List<ThreadEvaluator> threadEvaluators = new ArrayList<ThreadEvaluator>();
        int start = 0;
        for (int i = 0; i < threads; i++) {
            int evalsPerThisThread = minEvalsPerThread + ((i < remainingEvals) ? 1 : 0);
            threadEvaluators.add(
                    new ThreadEvaluator(stopLatch, perThreadConverters[i], perThreadEvaluators[i], population.subList(start, start + evalsPerThisThread))
            );
//            System.out.println(start + ", " + (start + evalsPerThisThread));
            start += evalsPerThisThread;
        }


        for (ThreadEvaluator threadEvaluator : threadEvaluators) {
//            threadEvaluator.start();
//            threadEvaluator.run();
            threadExecutor.execute(threadEvaluator);

        }
//        System.out.println("started threads");
        try {
            stopLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
//        System.out.println("all threads finished");

        EvaluationInfo[] evaluationInfos = new EvaluationInfo[population.size()];
        start = 0;
        for (ThreadEvaluator threadEvaluator : threadEvaluators) {
            EvaluationInfo[] fitnessPart = threadEvaluator.getEvaluationInfo();
            System.arraycopy(fitnessPart, 0, evaluationInfos, start, fitnessPart.length);
            start += fitnessPart.length;
        }

//        checkAgainstSequential(perThreadEvaluators[0], population, evaluationInfo);

        return evaluationInfos;
    }

    public EvaluationInfo evaluateGeneralization(G individual) {
        return perThreadEvaluators[0].evaluateGeneralization(perThreadConverters[0].convert(individual));
    }

    private EvaluationInfo[] sequentialEvaluate(List<G> population) {
        EvaluationInfo[] evaluationInfos = new EvaluationInfo[population.size()];
        for (int i = 0; i < population.size(); i++) {
            evaluationInfos[i] = perThreadEvaluators[0].evaluate(perThreadConverters[0].convert(population.get(i)));
        }
        return evaluationInfos;
    }

    private void checkAgainstSequential(List<G> population, EvaluationInfo[] evaluationInfos) {
        //checking parallel vs. sequential
        EvaluationInfo[] evaluationInfos2 = new EvaluationInfo[population.size()];
        for (int i = 0; i < population.size(); i++) {
            evaluationInfos2[i] = perThreadEvaluators[0].evaluate(perThreadConverters[0].convert(population.get(i)));
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
//        return 2;
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
        return perThreadEvaluators[0].getNumberOfInputs();
    }

    public int getNumberOfOutputs() {
        return perThreadEvaluators[0].getNumberOfOutputs();
    }

    /**
     * For direct methods only.
     * @return
     */
    public int getPhenotypeDimension() {
        if(perThreadConverters[0] instanceof DirectGenomeToINet) {
            return ((DirectGenomeToINet) perThreadConverters[0]).getNumOfLinks();
        } else {
            throw new IllegalStateException("getPhenotypeDimension() meant only for direct methods");
        }
    }

    public void shutdown() {
        threadExecutor.shutdownNow();
    }
}
