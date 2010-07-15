package opt.sade;

/**
 * <p>Title: SADE Library</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Anicka Kucerova, java version Jan Drchal
 * @version 1.0
 */

/**
 * This class implements the so-called objective or fitness function.
 * This is the function which is going to be optimized.
 */
public interface ObjectiveFunction {

    int getDim();

    double getDomain(int x, int y);

    boolean getReturnToDomain();

    boolean isSolved();

    /**
     * This method represents the objective function <i>f: <b>X</b>->Y</i>.
     *
     * @param oCH vector <i><b>X</b><i>
     * @return the value of <i>Y</i>
     */
    public double value(double[] oCH);

    public double generalizationValue(double[] oCH);
}