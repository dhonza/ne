package sneat.neuralnetwork.fastconcurrentnetwork;

import java.util.Comparator;

public class FloatFastConnectionComparer implements Comparator {

    public int compare(Object x, Object y) {
        FloatFastConnection a = (FloatFastConnection) x;
        FloatFastConnection b = (FloatFastConnection) y;


        int diff = a.sourceNeuronIdx - b.sourceNeuronIdx;
        if (diff == 0) {
            // Secondary sort on targetNeuronIdx.
            return a.targetNeuronIdx - b.targetNeuronIdx;
        } else {
            return diff;
        }
    }
}
