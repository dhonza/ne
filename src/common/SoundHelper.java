package common;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 7/5/11
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoundHelper {
    public static void playSoundFile(String fileName) {
        try {
            InputStream in = new FileInputStream(fileName);
            AudioStream as = new AudioStream(in);
            AudioPlayer.player.start(as);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
