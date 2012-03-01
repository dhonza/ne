package divvis;

import common.Bench;
import common.MatrixUtil;
import common.RND;

/**
 * User: drchaj1
 * Date: 31.3.2007
 * Time: 17:31:58
 */
public class BatchDiversityExperiment {
    static String[] methodStr = {"CG", "CGPAL", "QN", "SD", "SDF"};

    public static void makeExperiment(double[][] onormalizedD, EquationsInterface oequations, int oruns, boolean[] omethodAct) {
        int methods = methodStr.length;

        double[][] E = new double[methods][oruns]; //energy
        double[] EAvg = new double[methods];

        double[][] e = new double[methods][oruns]; //function evaluations
        double[] eAvg = new double[methods];

        double[][] g = new double[methods][oruns]; //gradient evaluations
        double[] gAvg = new double[methods];

        double[][] t = new double[methods][oruns]; //time
        double[] tAvg = new double[methods];

        OptimizeInterface optimizer = null;
        DistanceProjection prj;
        double[] xinit = null;
        Bench bench = new Bench();

        for (int i = 0; i < oruns; i++) {
            System.out.println("----- RUN: " + (i + 1) + "/" + oruns);
            boolean first = true;
            for (int j = 0; j < methods; j++) {
                if (omethodAct[j]) {
                    if (methodStr[j].equals("CG")) {
                        optimizer = new OptimizeConjugateGradient(onormalizedD, oequations);
                    } else if (methodStr[j].equals("CGPAL")) {
                        optimizer = new OptimizeConjugateGradientPAL(onormalizedD, oequations);
                    } else if (methodStr[j].equals("QN")) {
                        optimizer = new OptimizeQuasiNewton(onormalizedD, oequations);
                    } else if (methodStr[j].equals("SD")) {
                        optimizer = new OptimizeSteepestDescent(onormalizedD, oequations);
                    } else if (methodStr[j].equals("SDF")) {
                        optimizer = new OptimizeSteepestDescentFixedStep(onormalizedD, oequations);
                    } else {
                        System.out.println("ERROR BatchDiversityExperiment: unknown method");
                        System.exit(1);
                    }
                    prj = new DistanceProjection(onormalizedD, oequations, optimizer);
                    if (first) { //for all methods the same initial coordinates
                        xinit = prj.randomInitialize();
                        first = false;
                    }

                    bench.start();
                    prj.project(xinit.clone());
                    tAvg[j] += t[j][i] = bench.stop();
                    EAvg[j] += E[j][i] = prj.getEnergy();
                    eAvg[j] += e[j][i] = prj.getNumEvaluateCalls();
                    gAvg[j] += g[j][i] = prj.getNumGradientCalls();

//                    printProjection(prj);
                }
            }
        }

        for (int i = 0; i < methods; i++) {
            EAvg[i] /= oruns;
            eAvg[i] /= oruns;
            gAvg[i] /= oruns;
            tAvg[i] /= oruns;
        }
        //printing
        String s = "\t"; //separator

        System.out.print("RUN" + s);
        for (int i = 0; i < methods; i++) {
            if (omethodAct[i]) {
                System.out.print(methodStr[i] + "(E)" + s);
                System.out.print(methodStr[i] + "(FE)" + s);
                System.out.print(methodStr[i] + "(GE)" + s);
                System.out.print(methodStr[i] + "(ms)" + s);
            }
        }
        System.out.println("");

        for (int i = 0; i < oruns; i++) {
            System.out.print((i + 1) + s);
            for (int j = 0; j < methods; j++) {
                if (omethodAct[j]) {
                    System.out.print(E[j][i] + s);
                    System.out.print(e[j][i] + s);
                    System.out.print(g[j][i] + s);
                    System.out.print(t[j][i] + s);
                }
            }
            System.out.println("");
        }
        System.out.print("AVG" + s);
        for (int i = 0; i < methods; i++) {
            if (omethodAct[i]) {
                System.out.print(EAvg[i] + s);
                System.out.print(eAvg[i] + s);
                System.out.print(gAvg[i] + s);
                System.out.print(tAvg[i] + s);
            }
        }
        System.out.println("");

    }

    public static void printProjection(DistanceProjection oprj) {
        double[] x = new double[oprj.getN()];
        oprj.getProjectionXArray(x);
        double[] y = new double[oprj.getN()];
        oprj.getProjectionYArray(y);
        double[] z = new double[oprj.getN()];
        oprj.getProjectionZArray(z);
        for (int i = 0; i < x.length; i++) {
//            float[] cc = new float[3];
//            cols[i].getRGBColorComponents(cc);
            System.out.println(x[i] + " " + y[i] + " " + z[i] + " 1 0");
//            System.out.println(x[i] + " " + y[i] + " " + cc[0] + " " + cc[1] + " " + cc[2]);

        }
    }

    public static void main(String[] args) {
        RND.initializeTime();

//        int[] clusters = {2, 2, 2, 2};
//        int[] clusters = {2, 3, 15};
        int[] clusters = {2, 3, 15, 30};
//        int[] clusters = {2, 3, 15, 30, 75, 175};
//        int[] clusters = {25, 25, 50, 100, 200, 600};
        double[][] D = ArtificialClusterGenerator.generateClusters(clusters, 0.1, 0.1, 0.9, 0.9);
        MatrixUtil.normalize(D);

//methods:        {"CG", "CGPAL", "QN", "SD", "SDF"};
//        boolean[] methodAct = {true, false, true, true, false};
//        boolean[] methodAct = {true, true, true, false, false};
//        System.out.println("=======EquationsA medium coarse");
//        makeExperiment(D, new EquationsA(), 100, new boolean[]{false, false, false, false, true});
//        System.out.println("=======EquationsSammon medium coarse");
//        makeExperiment(D, new EquationsSammon(), 20, new boolean[]{true, true, false, true, false});

        makeExperiment(D, new EquationsA(), 5, new boolean[]{true, true, true, false, false});
//        makeExperiment(D, new EquationsA(), 5, new boolean[]{true, true, true, true, true});
    }
}
