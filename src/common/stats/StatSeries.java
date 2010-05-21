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
public class StatSeries {

    ArrayList<Double> data;

    public StatSeries() {
        this.data = new ArrayList<Double>();
    }

    public void addSample(double osample) {
        data.add(osample);
    }

    public void incrementOneToSample(int oidx) {
        if (data.size() <= oidx) {
            for (int i = data.size(); i <= oidx; i++) {
                data.add(0.0);
            }
        }
        double t = data.get(oidx);
        data.set(oidx, t + 1);
    }

    public double getSample(int oidx) {
        if (data.size() <= oidx) {
            for (int i = data.size(); i <= oidx; i++) {
                data.add(0.0);
            }
        }
        return data.get(oidx);
    }

    public void clear() {
        this.data.clear();
    }

    public int getSize() {
        return data.size();
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

    public String toString() {
        String s = "";
        for (int i = 0; i < data.size(); i++) {
            System.out.println(i + " " + data.get(i));
        }
        return s;
    }
}