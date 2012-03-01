package divvis.numopt.events;

import divvis.numopt.MinimizationMethod;

/**
 * User: honza
 * Date: 18.2.2007
 * Time: 21:26:51
 */
public class IterationEvent {//TODO maybe make this class OptimizationEvent descendant
    MinimizationMethod source;

    public IterationEvent(MinimizationMethod osource) {
        if (osource == null)
            throw new IllegalArgumentException("null source");

        this.source = osource;
    }

    public MinimizationMethod getSource() {
        return source;
    }

    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}
