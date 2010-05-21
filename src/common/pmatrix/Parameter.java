package common.pmatrix;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 25, 2009
 * Time: 8:10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Parameter <T> {
    final private String name;
    final private T value;

    public Parameter(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
