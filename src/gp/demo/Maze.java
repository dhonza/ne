package gp.demo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import common.evolution.EvaluationInfo;
import common.evolution.IBlackBox;
import common.evolution.IEvaluable;
import common.pmatrix.ParameterCombination;

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
public class Maze<P extends IBlackBox> implements IEvaluable<P> {
    final public static int EMPTY = 0;
    final public static int WALL = 1;
    final public static int START = 2;
    final public static int TARGET = 3;
    final public static int VISITED = 4;

    final protected static int[][] N = {{0, -1}, {-1, -1}, {1, -1}};//N, NW, NE
    final protected static int[][] S = {{0, 1}, {1, 1}, {-1, 1}};//S, SE, SW
    final protected static int[][] W = {{-1, 0}, {-1, 1}, {-1, -1}};
    final protected static int[][] E = {{1, 0}, {1, -1}, {1, 1}};

    protected int[][] map;
    protected int[] start = new int[2];
    protected int[] target = new int[2];//X, Y
    protected int[] pos = new int[2];//X, Y
    protected int[][] dir;


    protected int maxSteps;
    protected double[] inputs;
    protected boolean solved = false;

    public Maze(ParameterCombination combination) {
        maxSteps = combination.getInteger("MAZE.MAX_STEPS");
        String mapFile = combination.getString("MAZE.MAP");
        readMap("cfg/demo/maze/" + mapFile);
        setToStart();
        inputs = new double[getNumberOfInputs()];
    }


    public EvaluationInfo evaluate(P forest) {
        setToStart();

        ImmutableList.Builder<Telemetry> telemetryBuilder = ImmutableList.builder();
        int steps = 0;
        for (int i = 0; i < maxSteps; i++) {
            if (distanceToTarget() == 0) {
                solved = true;
                break;
            }
            readInputs(inputs);
            forest.loadInputs(inputs);
            forest.propagate();
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
            telemetryBuilder.add(new Telemetry(pos[0], pos[1]));
        }
//        double fitness = map.length + map[0].length + (maxSteps - steps) - distanceToTarget();
        double fitness = map.length + map[0].length - distanceToTarget();
        if (fitness < 0.0) {
            throw new IllegalStateException("Fitness < 0");
        }
        ImmutableMap<String, Object> paramMap = ImmutableMap.of(
                "FINAL_X", (Object) pos[0],
                "FINAL_Y", pos[1],
                "TELEMETRY", telemetryBuilder.build());
        return new EvaluationInfo(fitness, paramMap);
    }

    public EvaluationInfo evaluateGeneralization(P forest) {
        return evaluate(forest);
    }

    public void show(P forest) {
        evaluate(forest);
        System.out.println(this);
    }

    public boolean isSolved() {
        return solved;
    }

    public int getNumberOfInputs() {
        return 7;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public ImmutableList<Double> behavioralDiversityEndPosition(ImmutableList<EvaluationInfo> evaluationInfos) {
        ImmutableList.Builder<Double> builder = ImmutableList.builder();

        double maxDist = map.length + map[0].length;
        for (EvaluationInfo e1 : evaluationInfos) {
            int pos1x = (Integer) e1.getInfo("FINAL_X");
            int pos1y = (Integer) e1.getInfo("FINAL_Y");

            double sum = 0.0;
            for (EvaluationInfo e2 : evaluationInfos) {
                if (e1 != e2) {
                    int pos2x = (Integer) e2.getInfo("FINAL_X");
                    int pos2y = (Integer) e2.getInfo("FINAL_Y");
                    double dist = (Math.abs(pos1x - pos2x) + Math.abs(pos1y - pos2y)) / maxDist;
                    sum += dist;
                }
            }
            builder.add(sum / evaluationInfos.size());
//            builder.add(0.0);
        }
        return builder.build();
    }

    public ImmutableList<Double> behavioralDiversityTrack(ImmutableList<EvaluationInfo> evaluationInfos) {
        ImmutableList.Builder<Double> builder = ImmutableList.builder();

        for (EvaluationInfo e1 : evaluationInfos) {
            ImmutableList<Telemetry> track1 = (ImmutableList<Telemetry>) e1.getInfo("TELEMETRY");

            double sum = 0.0;
            for (EvaluationInfo e2 : evaluationInfos) {
                if (e1 != e2) {
                    ImmutableList<Telemetry> track2 = (ImmutableList<Telemetry>) e2.getInfo("TELEMETRY");
                    sum += this.trackDistance(track1, track2);
                }
            }
            builder.add(sum / evaluationInfos.size());
//            builder.add(0.0);
        }
        return builder.build();
    }

    protected void setToStart() {
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

    protected void moveF() {
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

    protected void rotateR() {
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


    protected void rotateL() {
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

    protected double distanceToTarget() {
        double dx = pos[0] - target[0];
        double dy = pos[1] - target[1];
        return Math.abs(dx) + Math.abs(dy);
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
//        in[cnt++] = distanceToTarget();
        in[cnt++] = map[pos[1] + dir[1][1]][pos[0] + dir[1][0]] == WALL ? 1.0 : 0.0;
        in[cnt++] = map[pos[1] + dir[0][1]][pos[0] + dir[0][0]] == WALL ? 1.0 : 0.0;
        in[cnt++] = map[pos[1] + dir[2][1]][pos[0] + dir[2][0]] == WALL ? 1.0 : 0.0;
        in[cnt++] = isF ? 1.0 : 0.0;
        in[cnt++] = isB ? 1.0 : 0.0;
        in[cnt++] = isL ? 1.0 : 0.0;
        in[cnt++] = isR ? 1.0 : 0.0;
    }

    protected void readMap(String mapFile) {
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
        for (int i = 0; i < getNumberOfInputs(); i++) {
            b.append(in[i]).append(" ");
        }
        return b.toString();
    }

    private class Telemetry {
        public final int x;
        public final int y;

        private Telemetry(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Telemetry{" + "x=" + x + ", y=" + y + '}';
        }
    }

    public double trackDistance(ImmutableList<Telemetry> a, ImmutableList<Telemetry> b) {
        int length = Ints.min(a.size(), b.size());
        double xSize = map[0].length;
        double ySize = map.length;
        double sum = 0.0;
        for (int i = 0; i < length; i++) {
            double dx = Math.abs(a.get(i).x - b.get(i).x) / xSize;
            double dy = Math.abs(a.get(i).y - b.get(i).y) / ySize;
            sum += dx + dy;
        }
        return sum / length;
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
