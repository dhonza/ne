package hyper.experiments.robots;


/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */

// This legacy VIVAE code has been commented out. Remove after rewriting to new interfaces!


public class Robots_New {

}
/*
public class Robots implements IProblem<INet>, IExperiment {
    private Arena arena = null;
    private Vector<Active> agents = null;

    private int inputs;
    private int hiddenOutputs;
    private String scenarioFileName;
    private boolean showGraphics;
    private int steps;

    private JFrame f = null;

    private int stepsDone;
    private ArrayList<IRobotInterface> robots = new ArrayList<IRobotInterface>();
    private ArrayList<VivaeController> controllers = new ArrayList<VivaeController>();

    public Robots(ParameterCombination parameters, ReportStorage reportStorage) {
        inputs = parameters.getInteger("ROBOTS.FRICTION_SENSORS");
        hiddenOutputs = parameters.getInteger("ROBOTS.NEURONS");
        scenarioFileName = parameters.getString("ROBOTS.SCENARIO");
        showGraphics = parameters.getBoolean("ROBOTS.SHOW_GRAPHICS");
        steps = parameters.getInteger("ROBOTS.STEPS");
    }

    public int getStepsDone() {
        return stepsDone;
    }

    public List<IRobotInterface> getRobots() {
        return robots;
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        robots.clear();
        controllers.clear();
        createArena(scenarioFileName, false);
        FitnessFunction avg = new AverageSpeed(this);

        setupExperiment(hyperNet);
//        arena.run();//TODO use this one!
        startExperiment(false);
        System.out.println("Fitness reached: " + avg.getFitness());

        return new EvaluationInfo(avg.getFitness());
    }

    public boolean isSolved() {
        return false;
    }

    public void show(INet hyperNet) {
        robots.clear();
        controllers.clear();
        createArena(scenarioFileName, true);
        FitnessFunction avg = new AverageSpeed(this);

        setupExperiment(hyperNet);
//        arena.run();//TODO use this one!
        startExperiment(true);
        System.out.println("Fitness reached: " + avg.getFitness());
    }

    public double getTargetFitness() {
        return 10.0;
    }

    public ISubstrate getSubstrate() {
        return RobotsSubstrateFactory.create(inputs, hiddenOutputs);
    }

    public List<String> getEvaluationInfoItemNames() {
        List<String> listOfNames = new ArrayList<String>();
        return listOfNames;
    }

    private void createArena(String svgFilename, boolean visible) {
        if (visible) {
            f = new JFrame("Hyper Experiment");
            arena = Arena.renewArena(f);
            arena.totalStepsPerSimulation = steps;

            f.setBounds(50, 0, arena.screenWidth, arena.screenHeight + 30);
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(arena);
            f.setVisible(true);

            arena.loadScenario(svgFilename);
            arena.isVisible = true;
            arena.setAllArenaPartsAntialiased(true);

        } else {
            arena = Arena.renewArena(f);
            arena.totalStepsPerSimulation = steps;
            arena.loadScenario(svgFilename);
            arena.isVisible = false;
            arena.setLoopSleepTime(0);
        }
//        arena.frictionBuffer = new FrictionBuffer(arena);

//        for(Active a: arena.getActives()) {
//            robots.add((IRobotInterface) a);
//        }

        agents = arena.getActives();
    }

    public void setupExperiment(INet hyperNet) {
        VivaeRobot va1 = new VivaeRobot("Vrobot1");

        double sangle = -Math.PI / 2;
        double eangle = +Math.PI / 2;
//        double ai = Math.PI / (sensors_cnt / 2 - 1);
        double ai = (eangle - sangle) / (inputs - 1);

        va1.addSensor(new ScalableDistanceSensor(va1, sangle, eangle, inputs, 50));
        va1.addSensor(new OdometerSensor(va1));
        robots.add(va1);
        NetController controller = new NetController(va1, hyperNet);
        controllers.add(controller);

        stepsDone = 0;
    }

    public void startExperiment(boolean isVisible) {
        int loopSleepTime = 10;
        int worldStepsPerLoop = 1;


        arena.setScreenSize(640, 480);
        arena.initWorld();

        for (int i = 1; i < 1500; i++) {
//            System.out.println("STEPSON: " + i);
            ListIterator<VivaeController> ci = controllers.listIterator();
            while (ci.hasNext()) {
                ci.next().moveControlledObject();
            }

            arena.getWorld().step();
            arena.moveVivaes();

            if (isVisible) {
                arena.repaint();
                if (loopSleepTime > 0) {
                    try {
                        Thread.sleep(loopSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            stepsDone++;
        }
    }
}
//*/
