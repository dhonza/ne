package gp.demo;

import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import common.pmatrix.ParameterCombination;
import gp.IGPForest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/13/11
 * Time: 11:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Maze implements IEvaluable<IGPForest> {
    final public static int EMPTY = 0;
    final public static int WALL = 1;
    final public static int START = 2;
    final public static int TARGET = 3;
    final public static int VISITED = 4;

    final private static int[][] N = {{0, -1}, {-1, -1}, {1, -1}};//N, NW, NE
    final private static int[][] S = {{0, 1}, {1, 1}, {-1, 1}};//S, SE, SW
    final private static int[][] W = {{-1, 0}, {-1, 1}, {-1, -1}};
    final private static int[][] E = {{1, 0}, {1, -1}, {1, 1}};

    private int[][] map;
    private int[] start = new int[2];
    private int[] target = new int[2];
    private int[] pos = new int[2];
    private int[][] dir;


    private int maxSteps = 30;
    private double[] inputs;
    private boolean solved = false;

    public Maze(ParameterCombination combination) {
        readMap("map1.csv");
        setToStart();
        inputs = new double[getNumberOfInputs()];
    }


    public EvaluationInfo evaluate(IGPForest forest) {
        setToStart();
        int steps = 0;
        for (int i = 0; i < maxSteps; i++) {
            if (distanceToTarget() == 0) {
                break;
            }
            readInputs(inputs);
            forest.loadInputs(inputs);
            double output = forest.getOutputs()[0];
            if (output < -0.5) {
                rotateL();
                moveF();
            } else if (output > 0.5) {
                rotateR();
                moveF();
            } else {
                moveF();
            }
            steps++;
        }
        double fitness = 30.0 + (maxSteps - steps) - distanceToTarget();
        return new EvaluationInfo(fitness);
    }

    public EvaluationInfo evaluateGeneralization(IGPForest forest) {
        return evaluate(forest);
    }

    public void show(IGPForest forest) {
        evaluate(forest);
        System.out.println(this);
    }

    public boolean isSolved() {
        return solved;
    }

    public int getNumberOfInputs() {
        return 8;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    private void setToStart() {
        pos[0] = start[0];
        pos[1] = start[1];
        dir = S;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == VISITED) {
                    map[i][j] = EMPTY;
                }
            }
        }
    }

    private void moveF() {
        if (map[pos[1]][pos[0]] == EMPTY) {
            map[pos[1]][pos[0]] = VISITED;
        }
        int posX = pos[0] + dir[0][0];
        int posY = pos[1] + dir[0][1];
        if (map[posY][posX] != WALL) {
            pos[0] = posX;
            pos[1] = posY;
        }
    }

    private void rotateR() {
        if (dir == N) {
            dir = E;
        } else if (dir == S) {
            dir = W;
        } else if (dir == W) {
            dir = N;
        } else if (dir == E) {
            dir = S;
        }
    }


    private void rotateL() {
        if (dir == N) {
            dir = W;
        } else if (dir == S) {
            dir = E;
        } else if (dir == W) {
            dir = S;
        } else if (dir == E) {
            dir = N;
        }
    }

    private double distanceToTarget() {
        double dx = pos[0] - target[0];
        double dy = pos[1] - target[1];
        return Math.abs(dx) + Math.abs(dy);
    }

    private void readInputs(double[] in) {
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

        in[0] = distanceToTarget();
        in[1] = map[pos[1] + dir[1][1]][pos[0] + dir[1][0]] == WALL ? 1.0 : 0.0;
        in[2] = map[pos[1] + dir[0][1]][pos[0] + dir[0][0]] == WALL ? 1.0 : 0.0;
        in[3] = map[pos[1] + dir[2][1]][pos[0] + dir[2][0]] == WALL ? 1.0 : 0.0;
        in[4] = isF ? 1.0 : 0.0;
        in[5] = isB ? 1.0 : 0.0;
        in[6] = isL ? 1.0 : 0.0;
        in[7] = isR ? 1.0 : 0.0;
    }

    private void readMap(String mapFile) {
        try {
            List<String> lines = new ArrayList<String>();
            FileInputStream fstream = new FileInputStream(mapFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                lines.add(strLine);
            }
            in.close();

            String[] line = lines.get(0).split(",");

            map = new int[lines.size()][line.length];

            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i).split(",");
                for (int j = 0, lineLength = line.length; j < lineLength; j++) {
                    map[i][j] = Integer.valueOf(line[j]);
                }
            }

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (map[i][j] == START) {
                        start[0] = j;
                        start[1] = i;
                    }
                    if (map[i][j] == TARGET) {
                        target[0] = j;
                        target[1] = i;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        int[] heading = {pos[0] + dir[0][0], pos[1] + dir[0][1]};
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (j == pos[0] && i == pos[1]) {
                    b.append('X');
                } else if (j == heading[0] && i == heading[1]) {
                    if (dir == S || dir == N) {
                        b.append('|');
                    } else {
                        b.append('-');
                    }
                } else {
                    if (map[i][j] == EMPTY) {
                        b.append('.');
                    } else if (map[i][j] == VISITED) {
                        b.append('x');
                    } else {
                        b.append(map[i][j]);
                    }
                }
            }
            b.append("\n");
        }
        b.append("inputs: ");
        double in[] = new double[getNumberOfInputs()];
        readInputs(in);
        b.append(in[0]).append(" ").append(in[1]).append(" ").append(in[2]).append(" ").append(in[3]);
        return b.toString();
    }

    public static void main(String[] args) {
        Maze m = new Maze(null);
        System.out.println(m);
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.rotateL();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.rotateR();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        m.moveF();
        System.out.println(m);
    }
}
