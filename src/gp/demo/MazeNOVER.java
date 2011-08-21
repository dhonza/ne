package gp.demo;

import common.pmatrix.ParameterCombination;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/18/11
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class MazeNOVER extends Maze {
    public MazeNOVER(ParameterCombination combination) {
        super(combination);
    }

    protected void readInputs(double[] in) {
        boolean isF, isB, isL, isR;
        if (dir == N) {
            isF = pos[1] >= target[1];
            isB = pos[1] < target[1];
            isL = pos[0] >= target[0];
            isR = pos[0] < target[0];
        } else if (dir == S) {
            isF = pos[1] <= target[1];
            isB = pos[1] > target[1];
            isL = pos[0] <= target[0];
            isR = pos[0] > target[0];
        } else if (dir == W) {
            isF = pos[0] >= target[0];
            isB = pos[0] < target[0];
            isL = pos[1] <= target[1];
            isR = pos[1] > target[1];
        } else {
            isF = pos[0] <= target[0];
            isB = pos[0] > target[0];
            isL = pos[1] >= target[1];
            isR = pos[1] < target[1];
        }
        int cnt = 0;
//        in[cnt++] = distanceToTarget();
        in[cnt++] = map[pos[1] + dir[1][1]][pos[0] + dir[1][0]] == WALL ? 1.0 : 0.0;
        in[cnt++] = map[pos[1] + dir[0][1]][pos[0] + dir[0][0]] == WALL ? 1.0 : 0.0;
        in[cnt++] = map[pos[1] + dir[2][1]][pos[0] + dir[2][0]] == WALL ? 1.0 : 0.0;
        in[cnt++] = isF ? 1.0 : 0.0;
        in[cnt++] = isB ? 1.0 : 0.0;
        in[cnt++] = isL ? 1.0 : 0.0;
        in[cnt++] = isR ? 1.0 : 0.0;
    }
}
