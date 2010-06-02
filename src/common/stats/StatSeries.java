package common.stats;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 2, 2010
 * Time: 1:05:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StatSeries {
    void clear();

    int getSize();

    String getStatString();

    Object getValue(int idx);
}
