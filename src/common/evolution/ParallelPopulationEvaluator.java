package common.evolution;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 19, 2010
 * Time: 7:28:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParallelPopulationEvaluator<T> {
    private class ThreadEvaluator extends Thread {
        final private CountDownLatch stopLatch;
        final private Evaluable<T> evaluator;
        final private List<T> part;
        final private EvaluationInfo[] evaluationInfo;

        private ThreadEvaluator(CountDownLatch stopLatch, Evaluable<T> evaluator, List<T> part) {
            this.evaluator = evaluator;
            this.part = part;
            this.stopLatch = stopLatch;
            this.evaluationInfo = new EvaluationInfo[part.size()];
        }

        public void run() {
            for (int i = 0; i < part.size(); i++) {
                evaluationInfo[i] = evaluator.evaluate(part.get(i));
            }
            stopLatch.countDown();
        }

        public EvaluationInfo[] getEvaluationInfo() {
            return evaluationInfo;
        }
    }

    public EvaluationInfo[] evaluate(Evaluable<T>[] perThreadEvaluators, List<T> population) {
        //sequential run
        if (perThreadEvaluators.length == 1) {
            return sequentialEvaluate(perThreadEvaluators[0], population);
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
                    new ThreadEvaluator(stopLatch, perThreadEvaluators[i], population.subList(start, start + evalsPerThisThread))
            );
//            System.out.println(start + ", " + (start + evalsPerThisThread));
            start += evalsPerThisThread;
        }


        for (ThreadEvaluator threadEvaluator : threadEvaluators) {
            threadEvaluator.start();
//            threadEvaluator.run();
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

    private EvaluationInfo[] sequentialEvaluate(Evaluable<T> perThreadEvaluator, List<T> population) {
        EvaluationInfo[] evaluationInfos = new EvaluationInfo[population.size()];
        for (int i = 0; i < population.size(); i++) {
            evaluationInfos[i] = perThreadEvaluator.evaluate(population.get(i));
        }
        return evaluationInfos;
    }

    private void checkAgainstSequential(Evaluable<T> perThreadEvaluator, List<T> population, EvaluationInfo[] evaluationInfos) {
        //checking parallel vs. sequential
        EvaluationInfo[] evaluationInfos2 = new EvaluationInfo[population.size()];
        for (int i = 0; i < population.size(); i++) {
            evaluationInfos2[i] = perThreadEvaluator.evaluate(population.get(i));
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
