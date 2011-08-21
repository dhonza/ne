package gp.demo;

import common.pmatrix.ParameterCombination;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/13/11
 * Time: 11:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MazeRangeFinders8 extends Maze {

    public MazeRangeFinders8(ParameterCombination combination) {
        super(combination);
    }


    public int getNumberOfInputs() {
        return 12;
    }

    protected double[] rangeFinders8() {
        double[] ranges = new double[8];
        int[][][] order;
        if (dir == N) {
            order = new int[][][]{N, E, S, W};
        } else if (dir == E) {
            order = new int[][][]{E, S, W, N};
        } else if (dir == S) {
            order = new int[][][]{S, W, N, E};
        } else {
            order = new int[][][]{W, N, E, S};
        }
        for (int i = 0; i < 4; i++) {
            int[] dir = order[i][0];
            int[] p = pos.clone();
            int range = 0;
            do {
                p[0] += dir[0];
                p[1] += dir[1];
                range++;
            } while (map[p[1]][p[0]] != WALL);
            ranges[2 * i] = range;

            dir = order[i][1];
            p = pos.clone();
            range = 0;
            do {
                p[0] += dir[0];
                p[1] += dir[1];
                range++;
            } while (map[p[1]][p[0]] != WALL);
            ranges[2 * i + 1] = range;
        }
        return ranges;
    }

    protected void readInputs(double[] in) {
        boolean isF, isB, isL, isR;
        if (dir == N) {
            isF = pos[1] >= target[1];
            isB = pos[1] <= target[1];
            isL = pos[0] >= target[0];
            isR = pos[0] <= target[0];
        } else if (dir == S) {
            isF = pos[1] <= target[1];
            isB = pos[1] >= target[1];
            isL = pos[0] <= target[0];
            isR = pos[0] >= target[0];
        } else if (dir == W) {
            isF = pos[0] >= target[0];
            isB = pos[0] <= target[0];
            isL = pos[1] <= target[1];
            isR = pos[1] >= target[1];
        } else {
            isF = pos[0] <= target[0];
            isB = pos[0] >= target[0];
            isL = pos[1] >= target[1];
            isR = pos[1] <= target[1];
        }
        int cnt = 0;
        double[] ranges = rangeFinders8();
        for (double range : ranges) {
            in[cnt++] = range;
        }
        in[cnt++] = isF ? 1.0 : 0.0;
        in[cnt++] = isB ? 1.0 : 0.0;
        in[cnt++] = isL ? 1.0 : 0.0;
        in[cnt++] = isR ? 1.0 : 0.0;
    }
}
