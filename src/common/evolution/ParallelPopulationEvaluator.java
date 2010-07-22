package common.evolution;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
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
        final private GenotypeToPhenotype<G, P> converter;
        final private Evaluable<P> evaluator;
        final private List<G> part;
        final private EvaluationInfo[] evaluationInfo;

        private ThreadEvaluator(CountDownLatch stopLatch, GenotypeToPhenotype<G, P> converter, Evaluable<P> evaluator, List<G> part) {
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

    final private Executor threadExecutor = Executors.newCachedThreadPool();

    public EvaluationInfo[] evaluate(GenotypeToPhenotype<G, P>[] perThreadConverters, Evaluable<P>[] perThreadEvaluators, List<G> population) {
        //sequential run
        if (perThreadEvaluators.length == 1) {
            return sequentialEvaluate(perThreadConverters[0], perThreadEvaluators[0], population);
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

    public EvaluationInfo evaluateGeneralization(GenotypeToPhenotype<G, P>[] perThreadConverters, Evaluable<P>[] perThreadEvaluators, G individual) {
        return perThreadEvaluators[0].evaluateGeneralization(perThreadConverters[0].convert(individual));
    }

    private EvaluationInfo[] sequentialEvaluate(GenotypeToPhenotype<G, P> perThreadConvertor, Evaluable<P> perThreadEvaluator, List<G> population) {
        EvaluationInfo[] evaluationInfos = new EvaluationInfo[population.size()];
        for (int i = 0; i < population.size(); i++) {
            evaluationInfos[i] = perThreadEvaluator.evaluate(perThreadConvertor.convert(population.get(i)));
        }
        return evaluationInfos;
    }

    private void checkAgainstSequential(GenotypeToPhenotype<G, P> perThreadConvertor, Evaluable<P> perThreadEvaluator, List<G> population, EvaluationInfo[] evaluationInfos) {
        //checking parallel vs. sequential
        EvaluationInfo[] evaluationInfos2 = new EvaluationInfo[population.size()];
        for (int i = 0; i < population.size(); i++) {
            evaluationInfos2[i] = perThreadEvaluator.evaluate(perThreadConvertor.convert(population.get(i)));
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
}
