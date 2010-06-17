package hyper.experiments.findcluster;

import common.net.INet;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2010
 * Time: 6:36:32 PM
 * Adapted from  Jason Gauci's HyperNEAT C++ 3.0
 */
public class FindCluster {
    /*
    NOTES for original sources:
     generateSubstrate: creates substrate structure
     populateSubstrate: sets weight in previously created substrate
                        they set bias of CPPN to 0.3, possibility to use deltaX, deltaY
     processEvaluation: evaluate on a single configuration of squares
     processGroup: test on all configurations of small and big square
    */
    private int numNodesX;
    private int numNodesY;

    private int sizeMultiplier;

    public FindCluster() {
        numNodesX = 11;
        numNodesY = 11;
        sizeMultiplier = 1;
    }

    private double processEvaluation(INet net, int x1, int y1, int x1Big, int y1Big) {
        int halfSizeX = 0;
        int halfSizeY = 0;

        //TODO init substrate here


//        substrate.reinitialize();
//        substrate.dummyActivation();

        int smallRadius;
        int bigRadius;

        double filledValue;

        if (sizeMultiplier == 1) {
            smallRadius = 0;
            bigRadius = 1;
            filledValue = 1.0;
        } else if (sizeMultiplier == 3) {
            smallRadius = 1;
            bigRadius = 4;
            filledValue = (11.0 / 33.0) * (11.0 / 33.0);
        } else if (sizeMultiplier == 5) {
            smallRadius = 2;
            bigRadius = 7;
            filledValue = (11.0 / 55.0) * (11.0 / 55.0);
        } else if (sizeMultiplier == 9) {
            smallRadius = 4;
            bigRadius = 13;
            filledValue = (11.0 / 66.0) * (11.0 / 66.0);
        } else {
            throw new IllegalStateException("Unsupported size multiplier!");
        }

        double[][] in = new double[numNodesY][numNodesX];
        for (int mody = -1 * smallRadius; mody <= 1 * smallRadius; mody++) {
            for (int modx = -1 * smallRadius; modx <= 1 * smallRadius; modx++) {
                try {
                    in[y1 + mody][x1 + modx] = filledValue;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("INVALID LOCATION: " + ((y1Big + mody) - numNodesY / 2) + ',' + ((x1Big + modx) - numNodesX / 2));
                }
            }
        }

        for (int mody = -1 * bigRadius; mody <= 1 * bigRadius; mody++) {
            for (int modx = -1 * bigRadius; modx <= 1 * bigRadius; modx++) {
                try {
                    in[y1Big + mody][x1Big + modx] = filledValue;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("INVALID LOCATION: " + ((y1Big + mody) - numNodesY / 2) + ',' + ((x1Big + modx) - numNodesX / 2));
                }

            }
        }
        printMatrix(in);
        System.out.println("");
        return 0.0;
    }

    public void processGroup(INet net) {
        /*
        //You get 10 points just for entering the game, wahooo!
        individual->setFitness(10);
        individual->setUserData(FindClusterStats().toString());
        populateSubstrate(individual);
        */

        double fitness = 0;

        double maxFitness = 0;

        boolean solved = true;

        for (int y1 = 1; y1 < numNodesY; y1 += 2) {
            for (int x1 = 1; x1 < numNodesX; x1 += 2) {
                int y1Big = (y1 + numNodesY / 2) % numNodesY;
                int x1Big = (x1 + numNodesX / 2) % numNodesX;

                if (y1Big == 0) {
                    y1Big++;
                } else if (y1Big + 1 == numNodesY) {
                    y1Big--;
                }

                if (x1Big == 0) {
                    x1Big++;
                } else if (x1Big + 1 == numNodesX) {
                    x1Big--;
                }

                if (x1 > 0 && x1 + 1 < numNodesX) {
                    fitness += processEvaluation(net, x1, y1, x1, y1Big);
                    maxFitness += 30;
                    System.out.println("Testing " + x1 + ',' + y1 + " and big " + x1 + ',' + y1Big);
                } else {
                    System.out.println("Tried to test " + x1 + ',' + y1 + " and big " + x1 + ',' + y1Big + " but big was out of range");
                }

                if (y1 > 0 && y1 + 1 < numNodesY) {
                    fitness += processEvaluation(net, x1, y1, x1Big, y1);
                    maxFitness += 30;
                    System.out.println("Testing " + x1 + ',' + y1 + " and big " + x1Big + ',' + y1);
                } else {
                    System.out.println("Tried to test " + x1 + ',' + y1 + " and big " + x1Big + ',' + y1 + " but big was out of range");
                }

                fitness += processEvaluation(net, x1, y1, x1Big, y1Big);
                System.out.println("Testing " + x1 + ',' + y1 + " and big " + x1Big + ',' + y1Big);
                maxFitness += 30;
//                #if
//                FIND_CLUSTER_EXPERIMENT_DEBUG
//                CREATE_PAUSE(string("Error!: ") + toString(__LINE__));
//                #endif

            }
        }

//        individual - > reward(fitness);

        System.out.printf("fitness: " + fitness);

        if (fitness >= maxFitness * .95) {
            System.out.println("PROBLEM DOMAIN SOLVED!!!");
        }
    }


    public void processIndividualPostHoc(INet net) {
//        individual->setFitness(10);
//        individual->setUserData(FindClusterStats().toString());
//        populateSubstrate(individual);

        double fitness = 0;

        double maxFitness = 0;

        boolean solved = true;

        int testCases = 0;

        for (int y1 = 0; y1 < numNodesY; y1 += sizeMultiplier) {
            for (int x1 = 0; x1 < numNodesX; x1 += sizeMultiplier) {
                for (int y1Big = 0; y1Big < numNodesY; y1Big += sizeMultiplier) {
                    for (int x1Big = 0; x1Big < numNodesX; x1Big += sizeMultiplier) {
                        int smallRadius;
                        int bigRadius;

                        if (sizeMultiplier == 1) {
                            smallRadius = 0;
                            bigRadius = 1;
                        } else if (sizeMultiplier == 3) {
                            smallRadius = 1;
                            bigRadius = 4;
                        } else if (sizeMultiplier == 5) {
                            smallRadius = 2;
                            bigRadius = 7;
                        } else if (sizeMultiplier == 9) {
                            smallRadius = 4;
                            bigRadius = 13;
                        } else {
                            throw new IllegalStateException("Unsupported size multiplier!");
                        }

                        int dist = smallRadius + 1 + bigRadius + 1;

                        if (((x1 - x1Big) * (x1 - x1Big) + (y1 - y1Big) * (y1 - y1Big)) < (dist * dist)) {
                            continue;
                        }

                        if (y1Big - bigRadius < 0) {
                            continue;
                        } else if (y1Big + bigRadius >= numNodesY) {
                            continue;
                        }

                        if (x1Big - bigRadius < 0) {
                            continue;
                        } else if (x1Big + bigRadius >= numNodesX) {
                            continue;
                        }
                        if (y1 - smallRadius < 0) {
                            continue;
                        } else if (y1 + smallRadius >= numNodesY) {
                            continue;
                        }
                        if (x1 - smallRadius < 0) {
                            continue;
                        } else if (x1 + smallRadius >= numNodesX) {
                            continue;
                        }

                        testCases++;

                        fitness += processEvaluation(net, x1, y1, x1Big, y1Big);

                        maxFitness += 30;

                    }
                }
            }
        }

        System.out.println("TOTAL TEST CASES: " + testCases);


//        individual - > reward(fitness);

        if (fitness >= maxFitness * .95) {
            System.out.println("PROBLEM DOMAIN SOLVED!!!");
        }
    }

    void increaseResolution() {
        if (sizeMultiplier == 1) {
            numNodesX *= 3; //((numNodesX-1)*2)+1;
            numNodesY *= 3; //((numNodesY-1)*2)+1;
            sizeMultiplier *= 3;
        } else if (sizeMultiplier == 3) {
            numNodesX = (numNodesX * 5) / 3; //((numNodesX-1)*2)+1;
            numNodesY = (numNodesY * 5) / 3; //((numNodesY-1)*2)+1;
            sizeMultiplier = (sizeMultiplier * 5) / 3;
        }
    }

    void decreaseResolution() {
        if (sizeMultiplier == 3) {
            numNodesX /= 3;//((numNodesX-1)/2)+1;
            numNodesY /= 3;//((numNodesY-1)/2)+1;
            sizeMultiplier /= 3;
        } else if (sizeMultiplier == 5) {
            numNodesX = (numNodesX * 3) / 5; //((numNodesX-1)*2)+1;
            numNodesY = (numNodesY * 3) / 5; //((numNodesY-1)*2)+1;
            sizeMultiplier = (sizeMultiplier * 3) / 5;
        }
    }


    public static void printMatrix(double[][] m) {
        for (double[] row : m) {
            for (double e : row) {
                System.out.printf("%1.0f ", 5 * e);
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        FindCluster f = new FindCluster();
        f.increaseResolution();
        f.processGroup(null);

//        f.processIndividualPostHoc(null);
    }
}
