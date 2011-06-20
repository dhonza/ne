package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/7/11
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IMathematicaPrintable {
    String toMathematicaExpression();

    int getId();

    int getParentId();

    double getFitness();
}
