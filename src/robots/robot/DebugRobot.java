/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package robots.robot;

/**
 *
 * @author drchaj1
 */
public class DebugRobot implements IRobotInterface {

    public void setWheelSpeed(double left, double right) {
        System.out.println(left + " " + right);
    }
}
