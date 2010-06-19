package hyper.experiments.findcluster;

import common.mathematica.MathematicaUtils;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.Problem;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2010
 * Time: 6:36:32 PM
 * Adapted from  Jason Gauci's HyperNEAT C++ 3.0
 */
public class FindCluster implements Problem {
    /*
    NOTES for original sources:
     generateSubstrate: creates substrate structure
     populateSubstrate: sets weight in previously created substrate
                        they set bias of CPPN to 0.3, possibility to use deltaX, deltaY
     evaluateSingleConfiguration (here evaluateSingleConfiguration()): evaluate on a single configuration of squares
     processGroup (): test on multiple configurations of small and big square
     processIndividualPostHoc:  test on all - generalization test
    */

    private class Pattern {
        public double[] in;
        public double[] out;

        private Pattern(double[] in, double[] out) {
            this.in = in;
            this.out = out;
        }
    }

    private int numNodesX;
    private int numNodesY;

    private int sizeMultiplier;

    final private int activations;

    private boolean solved = false;

    public FindCluster(ParameterCombination parameters) {
        numNodesX = parameters.getInteger("FIND_CLUSTER.NODES_X");
        numNodesY = parameters.getInteger("FIND_CLUSTER.NODES_Y");
        sizeMultiplier = 1;
        this.activations = parameters.getInteger("NET_ACTIVATIONS");
    }

    private Pattern evaluateNetForSingleConfiguration(INet hyperNet, int x1, int y1, int x1Big, int y1Big) {
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

        double[] in = new double[numNodesY * numNodesX];
        for (int mody = -1 * smallRadius; mody <= 1 * smallRadius; mody++) {
            for (int modx = -1 * smallRadius; modx <= 1 * smallRadius; modx++) {
                try {
                    int y = y1 + mody;
                    int x = x1 + modx;
                    in[y * numNodesX + x] = filledValue;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("INVALID LOCATION: " + ((y1Big + mody) - numNodesY / 2) + ',' + ((x1Big + modx) - numNodesX / 2));
                }
            }
        }

        for (int mody = -1 * bigRadius; mody <= 1 * bigRadius; mody++) {
            for (int modx = -1 * bigRadius; modx <= 1 * bigRadius; modx++) {
                try {
                    int y = y1Big + mody;
                    int x = x1Big + modx;
                    in[y * numNodesX + x] = filledValue;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("INVALID LOCATION: " + ((y1Big + mody) - numNodesY / 2) + ',' + ((x1Big + modx) - numNodesX / 2));
                }

            }
        }

        if (hyperNet.getNumInputs() != in.length) {
            System.out.println("Probably a bias indexation problem!!!");
            System.exit(1);
        }

        hyperNet.initSetBias();//TODO zbytecne - prozkoumat a zdokumentovat, co se v tech sitich deje?
        hyperNet.loadInputsNotBias(in);
//        hyperNet.loadInputs(in);
        hyperNet.reset();
        for (int i = 0; i < activations; i++) {
            hyperNet.activate();
        }

        return new Pattern(in, hyperNet.getOutputValues());
    }

    private double evaluateSingleConfiguration(INet hyperNet, int x1, int y1, int x1Big, int y1Big) {
        double[] outputs = evaluateNetForSingleConfiguration(hyperNet, x1, y1, x1Big, y1Big).out;

        double largestValue = -Integer.MAX_VALUE;
        double smallestValue = Integer.MAX_VALUE; // not used
        int largestY = 0;
        int largestX = 0;

        for (int y2 = 0; y2 < numNodesY; y2++) {
            for (int x2 = 0; x2 < numNodesX; x2++) {
                double value = outputs[y2 * numNodesX + x2];

                if (value > largestValue) {
                    largestValue = value;
                    largestX = x2;
                    largestY = y2;
                }

                if (value < smallestValue) {
                    smallestValue = value;
                }

            }
        }

        return Math.max(0, 30 - ((largestX - x1Big) * (largestX - x1Big) + (largestY - y1Big) * (largestY - y1Big)));
    }

    public double evaluate(INet hyperNet) {
        double fitness = 0;
        double maxFitness = 0;

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
                    fitness += evaluateSingleConfiguration(hyperNet, x1, y1, x1, y1Big);
                    maxFitness += 30;
//                    System.out.println("Testing " + x1 + ',' + y1 + " and big " + x1 + ',' + y1Big);
                } else {
//                    System.out.println("Tried to test " + x1 + ',' + y1 + " and big " + x1 + ',' + y1Big + " but big was out of range");
                }

                if (y1 > 0 && y1 + 1 < numNodesY) {
                    fitness += evaluateSingleConfiguration(hyperNet, x1, y1, x1Big, y1);
                    maxFitness += 30;
//                    System.out.println("Testing " + x1 + ',' + y1 + " and big " + x1Big + ',' + y1);
                } else {
//                    System.out.println("Tried to test " + x1 + ',' + y1 + " and big " + x1Big + ',' + y1 + " but big was out of range");
                }

                fitness += evaluateSingleConfiguration(hyperNet, x1, y1, x1Big, y1Big);
//                System.out.println("Testing " + x1 + ',' + y1 + " and big " + x1Big + ',' + y1Big);
                maxFitness += 30;
//                #if
//                FIND_CLUSTER_EXPERIMENT_DEBUG
//                CREATE_PAUSE(string("Error!: ") + toString(__LINE__));
//                #endif
//                 substrate.updateFixedIterations(1);
            }
        }

//        if (fitness >= maxFitness * .95) {//original setting
//            solved = true;
//        }
        if (fitness >= maxFitness * .99) {
            solved = true;
        }

        return fitness;
    }

    /*
   public void processIndividualPostHoc(INet hyperNet) {
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

                       fitness += evaluateSingleConfiguration(hyperNet, x1, y1, x1Big, y1Big);

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
    */

    public boolean isSolved() {
        return solved;
    }

    public void show(INet hyperNet) {
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

                System.out.println("{");
                if (x1 > 0 && x1 + 1 < numNodesX) {
                    Pattern pattern = evaluateNetForSingleConfiguration(hyperNet, x1, y1, x1, y1Big);
                    printMatrix(pattern.in, numNodesX);
                    System.out.println(",");
                    printMatrix(pattern.out, numNodesX);
                    System.out.println("\n},\n{");
                }

                if (y1 > 0 && y1 + 1 < numNodesY) {
                    Pattern pattern = evaluateNetForSingleConfiguration(hyperNet, x1, y1, x1Big, y1);
                    printMatrix(pattern.in, numNodesX);
                    System.out.println(",");
                    printMatrix(pattern.out, numNodesX);
                    System.out.println("\n},\n{");
                }

                Pattern pattern = evaluateNetForSingleConfiguration(hyperNet, x1, y1, x1Big, y1Big);

                printMatrix(pattern.in, numNodesX);
                System.out.println(",");

                printMatrix(pattern.out, numNodesX);
                if (y1 >= (numNodesY - 2) && x1 >= (numNodesX - 2)) {
                    System.out.println("\n}");
                } else {
                    System.out.println("\n},");
                }
            }
        }
    }

    public double getTargetFitness() {
        return 30000;
    }


    public static void printMatrix(double[] m, int numNodesX) {
        System.out.print("{{");
        for (int i = 0; i < m.length; i++) {
            if ((i + 1) % numNodesX != 0) {
//                System.out.printf("%1.3f, ", 1 * m[i]);
                System.out.print(MathematicaUtils.toMathematica(1 * m[i]) + ", ");
            } else {
//                System.out.printf("%1.3f", 1 * m[i]);
                System.out.print(MathematicaUtils.toMathematica(1 * m[i]));
            }
            if ((i + 1) % numNodesX == 0) {
                System.out.print("}");
                if (i < m.length - 1) {
                    System.out.print(",\n{");
                }
            }
        }
        System.out.print("}");
    }

    public static void main(String[] args) {
        FindCluster f = new FindCluster(null);
        f.evaluate(null);

//        f.processIndividualPostHoc(null);
    }
}
