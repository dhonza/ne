package common.evolution;

/**
 * Created by drchajan on 16/06/14.
 */
public interface IDistanceWithPrepare<T, U> extends IDistance<T> {
    T prepare(U u);
}
