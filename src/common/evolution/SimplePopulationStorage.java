package common.evolution;

import common.parallel.ParallelTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 27, 2010
 * Time: 5:17:33 PM
 * Simplified population storage: phenomes and distance phenomes are the same.
 */
public class SimplePopulationStorage<G, P> implements IPopulationStorage<G, P, P> {
    final private List<G> genomes;
    final private List<P> phenomes;
    final private ExecutorService threadExecutor;
    final private List<IGenotypeToPhenotype<G, P>> perThreadConverters;

    private boolean loaded = false;
    private boolean converted = false;

    public SimplePopulationStorage(ExecutorService threadExecutor, List<IGenotypeToPhenotype<G, P>> perThreadConverters) {
        genomes = new ArrayList<G>();
        phenomes = new ArrayList<P>();
        this.threadExecutor = threadExecutor;
        this.perThreadConverters = perThreadConverters;
    }

    public G getGenome(int idx) {
        if(!loaded && !converted) {
            throw new IllegalStateException("Not yet loaded genotypes!");
        }
        return genomes.get(idx);
    }

    public P getPhenome(int idx) {
        checkConverted();
        return phenomes.get(idx);
    }

    public List<P> getPhenomes() {
        checkConverted();
        return phenomes;
    }

    public P getDistancePhenome(int idx) {
        return getPhenome(idx);
    }

    public void loadGenomes(List<G> genomes) {
        loaded = true;
        converted = false;
        this.genomes.clear();
        this.phenomes.clear();
        this.genomes.addAll(genomes);
    }

    public void convert() {
        checkLoaded();
        loaded = false;
        converted = true;

        ParallelTransform<G, P> parallelTransform = new ParallelTransform<G,P>(threadExecutor, perThreadConverters);
        List<P> phenomes = parallelTransform.transform(genomes);
        this.phenomes.addAll(phenomes);
        
    }

    private void checkLoaded() {
        if(!loaded) {
            throw new IllegalStateException("Not loaded genotypes, yet!");
        }
    }

    private void checkConverted() {
        if(!converted) {
            throw new IllegalStateException("Not converted from genotype, yet!");
        }
    }
}
