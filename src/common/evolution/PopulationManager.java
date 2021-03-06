package common.evolution;

import common.net.precompiled.PrecompiledFeedForwardNetDistance;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.DistanceFactory;
import hyper.evaluate.converter.DirectGenomeToINet;
import org.apache.commons.lang.ArrayUtils;

import java.util.HashMap;
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

    private class DistanceCache {
        private G i;
        private G j;

        private DistanceCache(G i, G j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DistanceCache that = (DistanceCache) o;

            if (!i.equals(that.i)) return false;
            if (!j.equals(that.j)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = i.hashCode();
            result = 31 * result + j.hashCode();
            return result;
        }
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
    private boolean computeGenomeDistanceMatrix = false;
    private boolean storeGenomeDistanceMatrix = false;
    private boolean computeGenomeDistanceProjection = false;
    private boolean computePhenomeDistanceMatrix = false;

    private Map<DistanceCache, Double> genomeDistanceCache = new HashMap<DistanceCache, Double>();

    private DefaultDistance defaultDistance = DefaultDistance.GENOTYPE;

    private boolean storeGenotypesMathematica;

    public PopulationManager(List<IGenotypeToPhenotype<G, P>> perThreadConverters, List<IEvaluable<P>> perThreadEvaluators) {
        genomeDistance = DistanceFactory.createGenomeDistance(null);
        this.perThreadConverters = perThreadConverters;
        this.perThreadEvaluators = perThreadEvaluators;
        populationStorage = new SimplePopulationStorage<G, P>(threadExecutor, perThreadConverters);
        populationEvaluator = new PopulationEvaluator<P>(threadExecutor, perThreadEvaluators, populationStorage);
    }

    public PopulationManager(ParameterCombination parameters, List<IGenotypeToPhenotype<G, P>> perThreadConverters, List<IEvaluable<P>> perThreadEvaluators) {
        this(perThreadConverters, perThreadEvaluators);

        genomeDistance = DistanceFactory.createGenomeDistance(parameters);
        phenomeDistance = (IDistance<P>) new PrecompiledFeedForwardNetDistance();

        if (parameters.contains("GENOTYPE_DIVERSITY") && parameters.getBoolean("GENOTYPE_DIVERSITY")) {
            computeGenomeDistanceMatrix = true;
        }
        if (parameters.contains("GENOTYPE_DISTANCE_MATRIX") && parameters.getBoolean("GENOTYPE_DISTANCE_MATRIX")) {
            storeGenomeDistanceMatrix = true;
        }
        if (parameters.contains("GENOTYPE_DISTANCE_PROJECTION") && parameters.getBoolean("GENOTYPE_DISTANCE_PROJECTION")) {
            computeGenomeDistanceProjection = true;
        }
        if (parameters.contains("PHENOTYPE_DIVERSITY") && parameters.getBoolean("PHENOTYPE_DIVERSITY")) {
            computePhenomeDistanceMatrix = true;
        }
        if (parameters.contains("DISTANCE") && parameters.getString("DISTANCE").toLowerCase().equals("genotype")) {
            defaultDistance = DefaultDistance.GENOTYPE;
        } else if (parameters.contains("DISTANCE") && parameters.getString("DISTANCE").toLowerCase().equals("phenotype")) {
            defaultDistance = DefaultDistance.PHENOTYPE;
        }
        if (parameters.contains("STORE_GENOTYPES_MATHEMATICA")) {
            storeGenotypesMathematica = parameters.getBoolean("STORE_GENOTYPES_MATHEMATICA");
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
        genomeDistanceCache.clear();

        populationStorage.convert();
//        checkAgainstSequential(evaluationInfo);
        if (computeGenomeDistanceMatrix && genomeDistance != null) {
            getGenomeDistanceStorage().recompute();
        }
        if (computePhenomeDistanceMatrix && phenomeDistance != null) {
            getPhenomeDistanceStorage().recompute();
        }
        return populationEvaluator.evaluate();
    }

    public void showBSF(G bestSoFar) {
        perThreadEvaluators.get(0).show((P) bestSoFar);
    }

    public List<EvaluationInfo> evaluateNoDistances() {
        populationStorage.convert();
        System.out.println("WARNING: evaluateNoDistances() - distances not properly computed!");
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

    public double getGenomeDistance(G a, G b) {
        DistanceCache item = new DistanceCache(a, b);
        Double d = genomeDistanceCache.get(item);
        if (d == null) {
            d = genomeDistanceCache.get(new DistanceCache(b, a));
        }
        if (d == null) {
            d = genomeDistance.distance(a, b);
            genomeDistanceCache.put(item, d);
        }
        return d;
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
            if (storeGenomeDistanceMatrix) {
                infoMap.put("G_DISTANCE_MATRIX", genomeDistanceStorage.toString());
            }
            if (computeGenomeDistanceProjection) {
                infoMap.put("G_DISTANCE_PROJECTION", genomeDistanceStorage.project().toString());
            }
        }
        if (phenomeDistanceStorage != null) {
            infoMap.put("P_DIVERSITY", DistanceUtils.average(phenomeDistanceStorage));
        }
        if (storeGenotypesMathematica) {
            infoMap.put("GENOMES_MATH", populationStorage.genomesToMathematicaString());

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

    public IEvaluable<P> getFirstEvaluable() {
        return perThreadEvaluators.get(0);
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
