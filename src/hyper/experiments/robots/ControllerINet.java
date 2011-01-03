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
public class ControllerINet implements IRobotController {
    private IRobotInterface robot;
    private INet net;

    public ControllerINet(IRobotInterface robot, INet net) {
        this.robot = robot;
        this.net = net;
    }


    public void step() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public IRobotInterface getRobot() {
        return robot;
    }
}
