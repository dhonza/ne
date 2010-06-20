package common.evolution;

import gp.Forest;
import hyper.evaluate.GPEvaluator;
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
        final private double[] fitness;

        private ThreadEvaluator(CountDownLatch stopLatch, Evaluable<T> evaluator, List<T> part) {
            this.evaluator = evaluator;
            this.part = part;
            this.stopLatch = stopLatch;
            this.fitness = new double[part.size()];
        }

        public void run() {
            System.out.println("EVALUATOR: " + evaluator +
                    " SB: " + ((GPEvaluator)evaluator).getSubstrateBuilder() +
                    " PROBLEM: " + ((GPEvaluator)evaluator).getProblem());
            for (int i = 0; i < part.size(); i++) {
                fitness[i] = evaluator.evaluate(part.get(i));
            }
            stopLatch.countDown();
        }

        public double[] getFitness() {
            return fitness;
        }
    }

    public double[] evaluate(Evaluable<T>[] perThreadEvaluators, List<T> population) {
        double[] fitness2 = new double[population.size()];
        for (int i = 0; i < population.size(); i++) {
            fitness2[i] = perThreadEvaluators[0].evaluate(population.get(i));
        }

        int threads = ParallelPopulationEvaluator.getNumberOfThreads();

        System.out.println("Using threads:" + threads);
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

        double[] fitness = new double[population.size()];
        start = 0;
        for (ThreadEvaluator threadEvaluator : threadEvaluators) {
            double[] fitnessPart = threadEvaluator.getFitness();
            System.arraycopy(fitnessPart, 0, fitness, start, fitnessPart.length);
            start += fitnessPart.length;
        }

        System.out.println(ArrayUtils.toString(fitness));
        System.out.println(ArrayUtils.toString(fitness2));

        if(ArrayUtils.isEquals(fitness, fitness2)) {
            System.out.println("OK");
        } else {
            System.out.println("different");
        }

        return fitness;
    }

    public static int getNumberOfThreads() {
//        return Runtime.getRuntime().availableProcessors();
        return 2;
    }
}
