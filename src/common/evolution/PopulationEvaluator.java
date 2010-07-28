package common.evolution;

import common.parallel.ItemTransformer;
import common.parallel.ParallelTransform;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 28, 2010
 * Time: 11:35:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class PopulationEvaluator<P> {
    private class EvaluationMediator<P> implements ItemTransformer<P, EvaluationInfo> {
        final private IEvaluable<P> evaluator;

        private EvaluationMediator(IEvaluable<P> evaluator) {
            this.evaluator = evaluator;
        }

        public EvaluationInfo transform(P phenome) {
            return evaluator.evaluate(phenome);
        }
    }

    final private ExecutorService threadExecutor;
    final private List<IEvaluable<P>> perThreadEvaluators;
    final private IPopulationStorage<?, P, ?> populationStorage;
    final private List<EvaluationMediator<P>> transformers;

    public PopulationEvaluator(ExecutorService threadExecutor, List<IEvaluable<P>> perThreadEvaluators, IPopulationStorage<?, P, ?> populationStorage) {
        this.threadExecutor = threadExecutor;
        this.perThreadEvaluators = perThreadEvaluators;
        this.populationStorage = populationStorage;
        transformers = new ArrayList<EvaluationMediator<P>>();
        for (IEvaluable<P> perThreadEvaluator : perThreadEvaluators) {
            transformers.add(new EvaluationMediator<P>(perThreadEvaluator));
        }
    }

    public List<EvaluationInfo> evaluate() {

        ParallelTransform<P, EvaluationInfo> parallelTransform = new ParallelTransform<P, EvaluationInfo>(threadExecutor, transformers);
        return parallelTransform.transform(populationStorage.getPhenomes());
    }
}
