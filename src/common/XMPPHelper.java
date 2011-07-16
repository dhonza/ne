package common;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 7/13/11
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMPPHelper {
    public static void sendViaXMPP(String message) {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            if (hostName.equals("mbp.local")) {
                return;
            }
            ProcessBuilder pb = new ProcessBuilder("sendxmpp", "dhonza@gmail.com");
            Map<String, String> m = System.getenv();
            Process p = pb.start();

            message = hostName + ": " + message;

            OutputStream stdin = p.getOutputStream();
            stdin.write(message.getBytes());
            stdin.flush();
            stdin.close();
        } catch (IOException e) {
//            e.printStackTrace();
            System.err.println("Unable to send jabber message!");
        }
    }

    public static void main(String[] args) {
        sendViaXMPP("Message by NE!");
    }
}
