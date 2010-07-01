package hyper.experiments.findcluster;

import common.evolution.EvaluationInfo;
import common.mathematica.MathematicaUtils;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.Problem;
import hyper.experiments.reco.ReportStorage;
import hyper.substrate.Substrate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private final ReportStorage reportStorage;

    private int numNodesX;
    private int numNodesY;

    private int sizeMultiplier;

    final private int activations;

    private boolean solved = false;

    private double fitnessThreshold = 0.95;

    private List<Double> distances = new ArrayList<Double>();

    public FindCluster(ParameterCombination parameters, ReportStorage reportStorage) {
        numNodesX = parameters.getInteger("FIND_CLUSTER.NODES_X");
        numNodesY = parameters.getInteger("FIND_CLUSTER.NODES_Y");
        if (parameters.contains("FIND_CLUSTER.FITNESS_THRESHOLD")) {
            fitnessThreshold = parameters.getDouble("FIND_CLUSTER.FITNESS_THRESHOLD");
        }
        sizeMultiplier = 1;
        this.activations = parameters.getInteger("NET_ACTIVATIONS");
        this.reportStorage = reportStorage;
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
        double distance = (largestX - x1Big) * (largestX - x1Big) + (largestY - y1Big) * (largestY - y1Big);
        distances.add(distance);
        return Math.max(0, 30 - distance);
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        distances.clear();
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

        if (fitness >= maxFitness * fitnessThreshold) {//original setting
            solved = true;
        }

        double avg = 0;
        for (Double distance : distances) {
            avg += distance;
        }
        avg /= distances.size();

        Map<String, Object> infoMap = new LinkedHashMap<String, Object>();
        infoMap.put("AVG_DISTANCE", avg);

        return new EvaluationInfo(fitness, infoMap);
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

       if (fitness >= maxFitness * fitnessThreshold) {
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
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
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

                builder.append("{\n");
                if (x1 > 0 && x1 + 1 < numNodesX) {
                    Pattern pattern = evaluateNetForSingleConfiguration(hyperNet, x1, y1, x1, y1Big);
                    printMatrix(builder, pattern.in, numNodesX);
                    builder.append(",\n");
                    printMatrix(builder, pattern.out, numNodesX);
                    builder.append("\n},\n{\n");
                }

                if (y1 > 0 && y1 + 1 < numNodesY) {
                    Pattern pattern = evaluateNetForSingleConfiguration(hyperNet, x1, y1, x1Big, y1);
                    printMatrix(builder, pattern.in, numNodesX);
                    builder.append(",\n");
                    printMatrix(builder, pattern.out, numNodesX);
                    builder.append("\n},\n{\n");
                }

                Pattern pattern = evaluateNetForSingleConfiguration(hyperNet, x1, y1, x1Big, y1Big);

                printMatrix(builder, pattern.in, numNodesX);
                builder.append(",\n");

                printMatrix(builder, pattern.out, numNodesX);
                if (y1 >= (numNodesY - 2) && x1 >= (numNodesX - 2)) {
                    builder.append("\n}\n");
                } else {
                    builder.append("\n},\n");
                }
            }
        }
        builder.append("}\n");
        reportStorage.storeSingleRunAuxiliaryFile("find_cluster_", builder.toString());
    }

    public Substrate getSubstrate() {
        return FindClusterSubstrateFactory.createInputToOutputNoBias(numNodesX, numNodesY);
    }

    public double getTargetFitness() {
        return 30000;
    }


    public static void printMatrix(StringBuilder builder, double[] m, int numNodesX) {
        builder.append("{{");
        for (int i = 0; i < m.length; i++) {
            if ((i + 1) % numNodesX != 0) {
                builder.append(MathematicaUtils.toMathematica(1 * m[i])).append(", ");
            } else {
                builder.append(MathematicaUtils.toMathematica(1 * m[i]));
            }
            if ((i + 1) % numNodesX == 0) {
                builder.append("}");
                if (i < m.length - 1) {
                    builder.append(",\n{");
                }
            }
        }
        builder.append("}");
    }

    public static void main(String[] args) {
        FindCluster f = new FindCluster(null, null);
        f.evaluate(null);

//        f.processIndividualPostHoc(null);
    }
}
