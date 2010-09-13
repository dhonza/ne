package common.evolution;

import common.net.precompiled.PrecompiledFeedForwardNetDistance;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.DistanceFactory;
import hyper.evaluate.converter.DirectGenomeToINet;
import org.apache.commons.lang.ArrayUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private enum DefaultDistance {
        GENOTYPE,
        PHENOTYPE
    }

    final private SimplePopulationStorage<G, P> populationStorage;
    final private PopulationEvaluator<P> populationEvaluator;

    final private List<IGenotypeToPhenotype<G, P>> perThreadConverters;
    final private List<IEvaluable<P>> perThreadEvaluators;
    private IDistanceStorage genomeDistanceStorage = null;
    private IDistanceStorage phenomeDistanceStorage = null;

    final private ExecutorService threadExecutor = Executors.newCachedThreadPool();

    private IDistance<G> genomeDistance = null;
    private IDistance<P> phenomeDistance = null;

    private DefaultDistance defaultDistance = DefaultDistance.GENOTYPE;

    public PopulationManager(List<IGenotypeToPhenotype<G, P>> perThreadConverters, List<IEvaluable<P>> perThreadEvaluators) {
        this.perThreadConverters = perThreadConverters;
        this.perThreadEvaluators = perThreadEvaluators;
        populationStorage = new SimplePopulationStorage<G, P>(threadExecutor, perThreadConverters);
        populationEvaluator = new PopulationEvaluator<P>(threadExecutor, perThreadEvaluators, populationStorage);
    }

    public PopulationManager(ParameterCombination parameters, List<IGenotypeToPhenotype<G, P>> perThreadConverters, List<IEvaluable<P>> perThreadEvaluators) {
        this(perThreadConverters, perThreadEvaluators);
        if (parameters.contains("GENOTYPE_DIVERSITY") && parameters.getBoolean("GENOTYPE_DIVERSITY")) {
            genomeDistance = DistanceFactory.createGenomeDistance(parameters);
        }
        if (parameters.contains("PHENOTYPE_DIVERSITY") && parameters.getBoolean("PHENOTYPE_DIVERSITY")) {
            //TODO move this to the Problem
            phenomeDistance = (IDistance<P>) new PrecompiledFeedForwardNetDistance();
        }
        if (parameters.contains("DISTANCE") && parameters.getString("DISTANCE").toLowerCase().equals("genotype")) {
            defaultDistance = DefaultDistance.GENOTYPE;
        } else if (parameters.contains("DISTANCE") && parameters.getString("DISTANCE").toLowerCase().equals("phenotype")) {
            defaultDistance = DefaultDistance.PHENOTYPE;
        }
    }

    private IDistanceStorage getGenomeDistanceStorage() {
        if (genomeDistanceStorage == null) {
            genomeDistanceStorage = new SimpleDistanceStorage<G>(populationStorage.getGenomes(), populationStorage.getPreviousGenomes(), genomeDistance);
        }

        return genomeDistanceStorage;
    }

    private IDistanceStorage getPhenomeDistanceStorage() {
        if (phenomeDistanceStorage == null) {
            phenomeDistanceStorage = new SimpleDistanceStorage<P>(populationStorage.getDistancePhenomes(), populationStorage.getPreviousDistancePhenomes(), phenomeDistance);
        }
        return phenomeDistanceStorage;
    }

    public void loadGenotypes(List<G> population) {
        populationStorage.loadGenomes(population);
    }

    public List<EvaluationInfo> evaluate() {
        populationStorage.convert();
//        checkAgainstSequential(evaluationInfo);
        if (genomeDistance != null) {
            getGenomeDistanceStorage().recompute();
        }
        if (phenomeDistance != null) {
            getPhenomeDistanceStorage().recompute();
        }
        return populationEvaluator.evaluate();
    }

    public EvaluationInfo evaluateGeneralization(G individual) {
        return perThreadEvaluators.get(0).evaluateGeneralization(perThreadConverters.get(0).transform(individual));
    }

    public double getDistance(int idxA, int idxB) {
        if (defaultDistance == DefaultDistance.GENOTYPE) {
            return genomeDistanceStorage.distance(idxA, idxB);
        } else {
            return phenomeDistanceStorage.distance(idxA, idxB);
        }
    }

    public double getDistanceToPrevious(int idxCur, int idxPrev) {
        if (defaultDistance == DefaultDistance.GENOTYPE) {
            return genomeDistanceStorage.distanceToPrev(idxCur, idxPrev);
        } else {
            return phenomeDistanceStorage.distanceToPrev(idxCur, idxPrev);
        }
    }


    public BasicInfo getPopulationInfo() {
        Map<String, Object> infoMap = new LinkedHashMap<String, Object>();
        if (genomeDistanceStorage != null) {
            infoMap.put("G_DIVERSITY", DistanceUtils.average(genomeDistanceStorage));
        }
        if (phenomeDistanceStorage != null) {
            infoMap.put("P_DIVERSITY", DistanceUtils.average(phenomeDistanceStorage));
        }
        return new BasicInfo(infoMap);
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
