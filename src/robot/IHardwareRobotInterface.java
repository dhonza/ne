/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package robot;

import robot.eye.EyeImage;

import java.io.IOException;
import java.util.Map;

/**
 * @author drchaj1
 */
public interface IHardwareRobotInterface extends IRobotInterface {

    void connect() throws IOException;

    void disconnect() throws IOException;

    void setMaxWheelSpeed(double speed) throws IOException;

    EyeImage getEyeImage(String what, boolean capture) throws IOException;

    Map<String, Object> getStatus() throws IOException;

    String getHostname();

    int getPort();

}
