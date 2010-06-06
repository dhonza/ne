package hyper.evaluate;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Solver extends Serializable {
    public void solve();

    public String getConfigString();
}
