package common.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: drchal
 * Date: 21.12.2005
 * Time: 17:31:59
 * To change this template use File | Settings | File Templates.
 */
public class LongStatSeries implements StatSeries {

    final private ArrayList<Long> data;
    private long min = Long.MAX_VALUE;
    private long max = -Long.MAX_VALUE;

    public LongStatSeries() {
        this.data = new ArrayList<Long>();
    }

    public void addSample(long sample) {
        data.add(sample);
        checkMax(sample);
        checkMin(sample);
    }

    public void incrementOneToSample(int idx) {
        if (data.size() <= idx) {
            for (int i = data.size(); i <= idx; i++) {
                data.add(0l);
            }
        }
        long t = data.get(idx) + 1;
        data.set(idx, t);
        checkMax(t);
    }

    private void checkMin(long sample) {
        if (sample < min) {
            min = sample;
        }
    }

    private void checkMax(long sample) {
        if (sample > max) {
            max = sample;
        }
    }

    public double getSample(int idx) {
        if (data.size() <= idx) {
            for (int i = data.size(); i <= idx; i++) {
                data.add(0l);
            }
        }
        return data.get(idx);
    }

    public void clear() {
        this.data.clear();
        min = Long.MAX_VALUE;
        max = -Long.MAX_VALUE;
    }

    public int getSize() {
        return data.size();
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public long getMean() {
        if (data.size() == 0.0) {
            throw new IllegalStateException("Zero samples!");
        }
        double t = 0.0;
        for (Long aData : data) {
            t += aData;
        }
        return (long) (t / data.size());
    }

    private long getMeanDeviation(long omean) {
        if (data.size() == 0.0) {
            throw new IllegalStateException("Zero samples!");
        }
        double t = 0.0;
        for (Long xi : data) {
            t += (xi - omean) * (xi - omean);
        }
        return (long) (t / data.size());
    }

    public long getMeanDeviation() {
        return this.getMeanDeviation(this.getMean());//TODO no recomputation
    }

    public long getMeanDeviationSqrt() {
        return (long) Math.sqrt(this.getMeanDeviation(this.getMean()));
    }

    public long getMedian() {
        if (data.size() == 0.0) {
            throw new IllegalStateException("Zero samples!");
        }
        long[] t = new long[data.size()];
        int i = 0;
        for (Iterator<Long> iterator = data.iterator(); iterator.hasNext(); i++) {//TODO make better
            Long aLong = iterator.next();
            t[i] = aLong;
        }
        Arrays.sort(t);
        if (t.length % 2 == 0) {
            return (t[t.length / 2 - 1] + t[t.length / 2]) / 2;
        } else {
            return t[t.length / 2];
        }
    }

    public long[] getDataArray() {// TODO better
        long[] na = new long[data.size()];
        int i = 0;
        for (Long n : data) {
            na[i++] = n;
        }
        return na;
    }

    public Object getValue(int idx) {
        return data.get(idx);
    }

    public String getStatString() {
        return " Mean:" + getMean() + " Std.Dev.(SQRT):" + getMeanDeviationSqrt() +
                " Std.Dev.:" + getMeanDeviation() + " Median:" + getMedian() +
                " Range: (" + getMin() + " ... " + getMax() + ")";
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < data.size(); i++) {
            System.out.println(i + " " + data.get(i));
        }
        return s;
    }
}