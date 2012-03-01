package divvis;

/**
 * User: honza
 * Date: Jun 18, 2007
 * Time: 12:32:46 AM
 */
public class EquationsFactory {
    public static EquationsInterface createDefault() {
        return new EquationsA();
//        return new EquationsB();
//        return new EquationsSammon();
    }
}
