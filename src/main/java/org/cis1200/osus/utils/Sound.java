package org.cis1200.osus.utils;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {
    private Clip clip;
    private long clipTimePosition;

    public Sound(String soundName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (volume != null) {
                volume.setValue(-15.0f);
            }
            clipTimePosition = 0;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
        }
    }

    public void pause() {
        if (clip != null) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            clip.flush();
        }
    }
}
