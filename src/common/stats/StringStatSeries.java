package common.stats;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 2, 2010
 * Time: 1:06:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringStatSeries implements StatSeries {
    final private ArrayList<String> data;

    public StringStatSeries() {
        data = new ArrayList<String>();
    }

    public void addSample(String sample) {
        data.add(sample);
    }

    public void clear() {
        this.data.clear();
    }

    public int getSize() {
        return data.size();
    }

    public String[] getDataArray() {// TODO better
        String[] na = new String[data.size()];
        int i = 0;
        for (String n : data) {
            na[i++] = n;
        }
        return na;
    }

    public Object getValue(int idx) {
        return data.get(idx);
    }

    public String getStatString() {
        return "";
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < data.size(); i++) {
            System.out.println(i + " " + data.get(i));
        }
        return s;
    }
}