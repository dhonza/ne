package hyper.experiments.robots;

import common.net.INet;
import robot.IRobotInterface;
import robot.controller.IRobotController;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetController implements IRobotController {
    private IRobotInterface robot;
    private INet net;

    public NetController(IRobotInterface robot, INet net) {
        this.robot = robot;
        this.net = net;
        net.reset();
    }


    public void step() {
        double[] input = robot.getSensorData();
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
