package common.stats;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 2, 2010
 * Time: 1:06:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class BooleanStatSeries implements StatSeries {
    final private ArrayList<Boolean> data;

    public BooleanStatSeries() {
        data = new ArrayList<Boolean>();
    }

    public void addSample(boolean sample) {
        data.add(sample);
    }

    public void clear() {
        this.data.clear();
    }

    public int getSize() {
        return data.size();
    }

    public int getNumOfTrue() {
        int cnt = 0;
        for (Boolean aBoolean : data) {
            if (aBoolean) {
                cnt++;
            }
        }
        return cnt;
    }

    public int getNumOfFalse() {
        int cnt = 0;
        for (Boolean aBoolean : data) {
            if (!aBoolean) {
                cnt++;
            }
        }
        return cnt;
    }

    public double getTrueRate() {
        double numOfTrue = getNumOfTrue();
        double total = getSize();
        if (total == 0.0) {
            return 0.0;
        }
        return numOfTrue / total;
    }

    public boolean[] getDataArray() {// TODO better
        boolean[] na = new boolean[data.size()];
        int i = 0;
        for (Boolean n : data) {
            na[i++] = n;
        }
        return na;
    }

    public Object getValue(int idx) {
        return data.get(idx);
    }

    public String getStatString() {
        return " True Rate:" + getTrueRate() + " TRUE:" + getNumOfTrue() + " FALSE:" + getNumOfFalse() + " TOTAL:" + getSize();
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < data.size(); i++) {
            System.out.println(i + " " + data.get(i));
        }
        return s;
    }
}
