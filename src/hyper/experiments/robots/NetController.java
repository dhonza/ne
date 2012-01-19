package hyper.experiments.robots;

//import robot.IRobotInterface;
//import robot.controller.IRobotController;

import common.net.INet;
import vivae.controllers.RobotWithSensorController;
import vivae.robots.IRobotInterface;
import vivae.robots.IRobotWithSensorsInterface;
import vivae.util.Util;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */

// This legacy VIVAE code has been commented out. Remove after rewriting to new interfaces!

/*
public class NetController {

}
//*/

///*
public class NetController extends RobotWithSensorController {
    private final IRobotWithSensorsInterface robot;
    private INet net;

    public NetController(IRobotWithSensorsInterface robot, INet net) {
        this.robot = robot;
        this.net = net;
        net.reset();
    }

    @Override
    public void moveControlledObject() {
        step();
    }

    public void step() {
        double[] input = Util.flatten(robot.getSensorData());
//        ArrayHelper.printArray(input);
        net.initSetBias();//TODO zbytecne - prozkoumat a zdokumentovat, co se v tech sitich deje?
        net.loadInputs(input);
//        hyperNet.loadInputs(in);
        net.activate();
        double[] outputs = net.getOutputs();
//        System.out.print(" -> ");
//        ArrayHelper.printArray(outputs);
//        System.out.println();
        double left = outputs[0];
        double right = outputs[outputs.length - 1];
        robot.setWheelSpeed(5 * left, 5 * right);
    }

    public IRobotInterface getRobot() {
        return robot;
    }
}
//*/