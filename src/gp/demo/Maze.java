package gp.demo;

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
public class Maze {
    final public static int EMPTY = 0;
    final public static int WALL = 1;
    final public static int START = 2;
    final public static int TARGET = 3;

    final private static int[][] N = {{0, -1}, {-1, -1}, {1, -1}};//N, NW, NE
    final private static int[][] S = {{0, 1}, {1, 1}, {-1, 1}};//S, SE, SW
    final private static int[][] W = {{-1, 0}, {-1, 1}, {-1, -1}};
    final private static int[][] E = {{1, 0}, {1, -1}, {1, 1}};

    private int[][] map;
    private int[] start = new int[2];
    private int[] target = new int[2];
    private int[] pos = new int[2];
    private int[][] dir;

    public Maze() {
        readMap("map1.csv");
        setToStart();
    }

    private void setToStart() {
        pos[0] = start[0];
        pos[1] = start[1];
        dir = S;
    }

    private double distanceToTarget() {
        double dx = pos[0] - target[0];
        double dy = pos[1] - target[1];
        return Math.sqrt(dx * dx + dy + dy);
    }

    private void readInputs(double[] in) {
        in[0] = distanceToTarget();
        in[1] = map[pos[1] + dir[1][1]][pos[0] + dir[1][0]] == WALL ? 1.0 : 0.0;
        in[2] = map[pos[1] + dir[0][1]][pos[0] + dir[0][0]] == WALL ? 1.0 : 0.0;
        in[3] = map[pos[1] + dir[2][1]][pos[0] + dir[2][0]] == WALL ? 1.0 : 0.0;
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
                    } else {
                        b.append(map[i][j]);
                    }
                }
            }
            b.append("\n");
        }
        b.append("inputs: ");
        double in[] = new double[4];
        readInputs(in);
        b.append(in[0]).append(" ").append(in[1]).append(" ").append(in[2]).append(" ").append(in[3]);
        return b.toString();
    }

    public static void main(String[] args) {
        Maze m = new Maze();
        System.out.println(m);
    }
}
