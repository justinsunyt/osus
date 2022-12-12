package org.cis1200.osus.utils;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {
    public static void playSound(String soundName) {
        try {
            AudioInputStream audioInputStream = AudioSystem
                    .getAudioInputStream(new File(soundName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (volume != null) {
                volume.setValue(-15.0f);
            }
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
