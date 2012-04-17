package common;

import org.apache.commons.io.FileUtils;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 7/13/11
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMPPHelper {
    public static void sendViaXMPP(String message) {
        String userHome = System.getProperty("user.home");
        try {
            String jabberConfig = FileUtils.readFileToString(new File(userHome + "/.nexmpp"));
            String[] xmppParams = jabberConfig.split(" ");
            if (xmppParams.length != 4) {
                System.err.println("Wrong .nexmpp format, should be: server server_login server_password jabber_id");
                return;
            }
            String server = xmppParams[0];
            String serverLogin = xmppParams[1];
            String serverPassword = xmppParams[2];
            String jabberId = xmppParams[3].split("\n")[0];


            Calendar cal = Calendar.getInstance();
            int hour24 = cal.get(Calendar.HOUR_OF_DAY);
            if (hour24 <= 9) {//do not wake me up :)
                return;
            }
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
//                if (hostName.endsWith(".local") || hostName.startsWith("eduroam") || hostName.startsWith("dhcp")) {
//                    return;
//                }
                message = hostName + ": " + message;

                XMPPConnection connection = new XMPPConnection(server);
                connection.connect();
                connection.login(serverLogin, serverPassword);
                ChatManager chatmanager = connection.getChatManager();
                Chat chat = chatmanager.createChat(jabberId, null);
                chat.sendMessage(message);
                connection.disconnect();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("No " + userHome + "/.nexmpp file.");
            return;
        }
    }

    public static void main(String[] args) {
        sendViaXMPP("Message by NE!");
    }
}
