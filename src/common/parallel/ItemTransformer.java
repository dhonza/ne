package common.parallel;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 27, 2010
 * Time: 7:17:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ItemTransformer<F, T> {
    T transform(F from);
}
