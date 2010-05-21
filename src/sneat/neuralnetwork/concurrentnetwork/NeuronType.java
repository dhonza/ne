package sneat.neuralnetwork.concurrentnetwork;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Feb 11, 2010
 * Time: 7:50:02 PM
 * To change this template use File | Settings | File Templates.
 */
public enum NeuronType {
    INPUT("in"),
    BIAS("bias"),
    HIDDEN("hid"),
    OUTPUT("out"),
    UNDEFINED("undefined");

    private final String shortName;

    NeuronType(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public static NeuronType createFromShort(String shortName) {
        if (shortName.equals("in")) {
            return INPUT;
        } else if (shortName.equals("hid")) {
            return HIDDEN;
        } else if (shortName.equals("out")) {
            return OUTPUT;
        } else if (shortName.equals("bias")) {
            return BIAS;
        } else {
            return UNDEFINED;
        }
    }
}
