package common.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 27, 2010
 * Time: 7:08:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParallelTransform<S, T> {
    private class ThreadTransformer implements Runnable {
        final private CountDownLatch stopLatch;
        final private ItemTransformer<S, T> transformer;
        final private List<S> part;
        final private List<T> target;

        private ThreadTransformer(CountDownLatch stopLatch, ItemTransformer<S, T> transformer, List<S> part) {
            this.transformer = transformer;
            this.part = part;
            this.stopLatch = stopLatch;
            this.target = new ArrayList<T>();
        }

        public void run() {
            for (S sourceItem : part) {
                target.add(transformer.transform(sourceItem));
            }
            stopLatch.countDown();
        }

        public List<T> getTarget() {
            return target;
        }
    }

    final private ExecutorService threadExecutor;
    final private List<? extends ItemTransformer<S, T>> transformers;

    public ParallelTransform(ExecutorService threadExecutor, List<? extends ItemTransformer<S, T>> transformers) {
        this.threadExecutor = threadExecutor;
        this.transformers = transformers;
    }

    public List<T> transform(List<S> list) {
        int threads = transformers.size();
        if (threads > list.size()) {
            threads = list.size();
        }
        int minEvalsPerThread = list.size() / threads;
        int remainingEvals = list.size() % threads;

        CountDownLatch stopLatch = new CountDownLatch(threads);
        List<ThreadTransformer> threadTransformers = new ArrayList<ThreadTransformer>();
        int start = 0;
        for (int i = 0; i < threads; i++) {
            int evalsPerThisThread = minEvalsPerThread + ((i < remainingEvals) ? 1 : 0);
            threadTransformers.add(
                    new ThreadTransformer(stopLatch, transformers.get(i), list.subList(start, start + evalsPerThisThread))
            );
            start += evalsPerThisThread;
        }

        for (ThreadTransformer threadTransformer : threadTransformers) {
            threadExecutor.execute(threadTransformer);
        }

        try {
            stopLatch.await();
//            stopLatch.await(20L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        List<T> result = new ArrayList<T>(list.size());
        for (ThreadTransformer threadTransformer : threadTransformers) {
            result.addAll(threadTransformer.getTarget());
        }
        return result;
    }
}
