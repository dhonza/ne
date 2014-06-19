package gp.demo;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import common.evolution.EvaluationInfo;
import common.evolution.IDistanceWithPrepare;

/**
 * Created by drchajan on 16/06/14.
 */
public class MazeBehavioralDistanceFactory {
    public static IDistanceWithPrepare<Object, EvaluationInfo> createDistance(Maze maze, String name) {
        switch (name) {
            case "END_POSITION":
                return new DistanceEndPosition(maze);
            case "TRACK":
                return new DistanceTrack(maze);
            case "ORIENT":
                return new DistanceOrientation(maze);
            case "IO":
                return new DistanceIO(maze);
            case "IO_HAMMING":
                return new DistanceIOHamming(maze);
            default:
                throw new IllegalStateException("Unknown behavioral distance: " + name);

        }
    }

    protected static abstract class AbstractDistance implements IDistanceWithPrepare<Object, EvaluationInfo> {
        protected final Maze maze;

        protected AbstractDistance(Maze maze) {
            this.maze = maze;
        }
    }

    private static class DistanceEndPosition extends AbstractDistance {
        double maxDist;

        protected DistanceEndPosition(Maze maze) {
            super(maze);
            maxDist = maze.map.length + maze.map[0].length;
        }

        @Override
        public Object prepare(EvaluationInfo evaluationInfo) {
            return new int[]{(Integer) evaluationInfo.getInfo("FINAL_X"), (Integer) evaluationInfo.getInfo("FINAL_Y")};
        }

        @Override
        public double distance(Object a, Object b) {
            int[] pa = (int[]) a;
            int[] pb = (int[]) b;

            double dist = (Math.abs(pa[0] - pb[0]) + Math.abs(pa[1] - pb[1])) / maxDist;
            return dist;
        }
    }

    private static class DistanceTrack extends AbstractDistance {

        protected DistanceTrack(Maze maze) {
            super(maze);
        }

        @Override
        public Object prepare(EvaluationInfo evaluationInfo) {
            return evaluationInfo.getInfo("TELEMETRY");
        }

        @Override
        public double distance(Object a, Object b) {
            ImmutableList<Maze.Telemetry> ta = (ImmutableList<Maze.Telemetry>) a;
            ImmutableList<Maze.Telemetry> tb = (ImmutableList<Maze.Telemetry>) b;

            int length = Ints.min(ta.size(), tb.size());
            double xSize = maze.map[0].length;
            double ySize = maze.map.length;
            double sum = 0.0;
            for (int i = 0; i < length; i++) {
                double dx = Math.abs(ta.get(i).x - tb.get(i).x) / xSize;
                double dy = Math.abs(ta.get(i).y - tb.get(i).y) / ySize;
                sum += dx + dy;
            }
            return sum / length;
        }
    }

    private static class DistanceOrientation extends AbstractDistance {

        protected DistanceOrientation(Maze maze) {
            super(maze);
        }

        @Override
        public Object prepare(EvaluationInfo evaluationInfo) {
            return evaluationInfo.getInfo("TELEMETRY");
        }

        @Override
        public double distance(Object a, Object b) {
            ImmutableList<Maze.Telemetry> ta = (ImmutableList<Maze.Telemetry>) a;
            ImmutableList<Maze.Telemetry> tb = (ImmutableList<Maze.Telemetry>) b;

            int length = Ints.min(ta.size(), tb.size());
            double sum = 0.0;
            for (int i = 0; i < length; i++) {
                int ao = ta.get(i).orientation;
                int bo = tb.get(i).orientation;
                int d = Math.abs(ao - bo);
                sum += d == 3 ? 1 : d;
            }
            return sum / length;
        }
    }

    private static class DistanceIO extends AbstractDistance {

        protected DistanceIO(Maze maze) {
            super(maze);
        }

        @Override
        public Object prepare(EvaluationInfo evaluationInfo) {
            ImmutableList<Double> i = (ImmutableList<Double>) evaluationInfo.getInfo("INPUTS");
            ImmutableList<Double> o = (ImmutableList<Double>) evaluationInfo.getInfo("OUTPUTS");
            ImmutableList<ImmutableList<Double>> l = ImmutableList.of(i, o);
            return l;
        }

        @Override
        public double distance(Object a, Object b) {
            ImmutableList<ImmutableList<Double>> l1 = (ImmutableList<ImmutableList<Double>>) a;
            ImmutableList<Double> i1 = l1.get(0);
            ImmutableList<Double> o1 = l1.get(1);

            ImmutableList<ImmutableList<Double>> l2 = (ImmutableList<ImmutableList<Double>>) b;
            ImmutableList<Double> i2 = l2.get(0);
            ImmutableList<Double> o2 = l2.get(1);

            int s = Math.min(i1.size(), i2.size());
            double sumI = 0.0;
            for (int i = 0; i < s; i++) {
                sumI += Math.abs(i1.get(i) - i2.get(i));
            }
            sumI /= s;

            s = Math.min(o1.size(), o2.size());
            double sumO = 0.0;
            for (int i = 0; i < s; i++) {
                sumO += Math.abs(o1.get(i) - o2.get(i));
            }
            sumO /= s;

            return (sumI + sumO) / 2.0;
        }
    }

    private static class DistanceIOHamming extends AbstractDistance {

        protected DistanceIOHamming(Maze maze) {
            super(maze);
        }

        @Override
        public Object prepare(EvaluationInfo evaluationInfo) {
            ImmutableList<Double> i = (ImmutableList<Double>) evaluationInfo.getInfo("INPUTS");
            ImmutableList<Double> o = (ImmutableList<Double>) evaluationInfo.getInfo("OUTPUTS");
            ImmutableList<ImmutableList<Double>> l = ImmutableList.of(i, o);
            return l;
        }

        @Override
        public double distance(Object a, Object b) {
            ImmutableList<ImmutableList<Double>> l1 = (ImmutableList<ImmutableList<Double>>) a;
            ImmutableList<Double> i1 = l1.get(0);
            ImmutableList<Double> o1 = l1.get(1);

            ImmutableList<ImmutableList<Double>> l2 = (ImmutableList<ImmutableList<Double>>) b;
            ImmutableList<Double> i2 = l2.get(0);
            ImmutableList<Double> o2 = l2.get(1);

            int s = Math.min(i1.size(), i2.size());
            double sumI = 0.0;
            for (int i = 0; i < s; i++) {
                double ta = i1.get(i) < 0.5 ? 0.0 : 1.0;
                double tb = i2.get(i) < 0.5 ? 0.0 : 1.0;
                sumI += Math.abs(ta - tb);
            }
            sumI /= s;

            s = Math.min(o1.size(), o2.size());
            double sumO = 0.0;
            for (int i = 0; i < s; i++) {
                double ta = o1.get(i) < 0.5 ? 0.0 : 1.0;
                double tb = o2.get(i) < 0.5 ? 0.0 : 1.0;
                sumO += Math.abs(ta - tb);
            }
            sumO /= s;

            return (sumI + sumO) / 2.0;
        }
    }
}
