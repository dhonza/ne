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
public class DoubleStatSeries implements StatSeries {

    final private ArrayList<Double> data;
    private double min = Double.MAX_VALUE;
    private double max = -Double.MAX_VALUE;

    public DoubleStatSeries() {
        this.data = new ArrayList<Double>();
    }

    public void addSample(double sample) {
        data.add(sample);
        checkMax(sample);
        checkMin(sample);
    }

    public void incrementOneToSample(int idx) {
        if (data.size() <= idx) {
            for (int i = data.size(); i <= idx; i++) {
                data.add(0.0);
            }
        }
        double t = data.get(idx) + 1;
        data.set(idx, t);
        checkMax(t);
    }

    private void checkMin(double sample) {
        if (sample < min) {
            min = sample;
        }
    }

    private void checkMax(double sample) {
        if (sample > max) {
            max = sample;
        }
    }

    public double getSample(int idx) {
        if (data.size() <= idx) {
            for (int i = data.size(); i <= idx; i++) {
                data.add(0.0);
            }
        }
        return data.get(idx);
    }

    public void clear() {
        this.data.clear();
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    public int getSize() {
        return data.size();
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        if (data.size() == 0.0) return Double.NaN;
        double t = 0.0;
        for (Double aData : data) {
            t += aData;
        }
        return t / data.size();
    }

    private double getMeanDeviation(double omean) {
        if (data.size() == 0.0) return Double.NaN;
        double t = 0.0;
        for (Double xi : data) {
            t += (xi - omean) * (xi - omean);
        }
        return t / data.size();
    }

    public double getMeanDeviation() {
        return this.getMeanDeviation(this.getMean());//TODO no recomputation
    }

    public double getMeanDeviationSqrt() {
        return Math.sqrt(this.getMeanDeviation(this.getMean()));
    }

    public double getMedian() {
        if (data.size() == 0.0) return Double.NaN;
        double[] t = new double[data.size()];
        int i = 0;
        for (Iterator<Double> iterator = data.iterator(); iterator.hasNext(); i++) {//TODO make better
            Double aDouble = iterator.next();
            t[i] = aDouble;
        }
        Arrays.sort(t);
        if (t.length % 2 == 0) {
            return (t[t.length / 2 - 1] + t[t.length / 2]) / 2;
        } else {
            return t[t.length / 2];
        }
    }

    public double[] getDataArray() {// TODO better
        double[] na = new double[data.size()];
        int i = 0;
        for (Double n : data) {
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