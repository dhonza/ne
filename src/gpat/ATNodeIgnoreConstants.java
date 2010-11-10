package gpat;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 10, 2010
 * Time: 10:31:00 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class ATNodeIgnoreConstants extends ATNode {
    protected ATNodeIgnoreConstants(int id, int depth) {
        super(id, depth);
    }

    protected ATNodeIgnoreConstants() {
        super();
    }

    @Override
    public String toMathematicaExpression() {
        if (getArity() == 0) {
            return getName();
        } else {
            StringBuilder b = new StringBuilder(getName());
            b.append("[");
            for (int i = 0; i < children.size(); i++) {
                ATNode child = children.get(i);
                b.append(child.toMathematicaExpression());
                if (i < children.size() - 1) {
                    b.append(",");
                }
            }
            b.append("]");
            return b.toString();
        }
    }
}
