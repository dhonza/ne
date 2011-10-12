package common;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

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
        Calendar cal = Calendar.getInstance();
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);
        if (hour24 <= 9) {//do not wake me up :)
            return;
        }
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            if (hostName.equals("mbp.local") || hostName.startsWith("eduroam")) {
                return;
            }
            message = hostName + ": " + message;

            XMPPConnection connection = new XMPPConnection("jabber.iitsp.com");
            connection.connect();
            connection.login("hdadmin", "jabberpokusny");
            ChatManager chatmanager = connection.getChatManager();
            Chat chat = chatmanager.createChat("dhonza@gmail.com", null);
            chat.sendMessage(message);
            connection.disconnect();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendViaXMPP("Message by NE!");
    }
}
