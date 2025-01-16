package src.Model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip clip;

    /**
     * Plays the music file from the beginning.
     *
     * @param filePath the path of the music file.
     */
    public void playMusic(String filePath) {
        stopMusic();

        try {
            File musicFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.err.println("Error playing music: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Stops the music playback.
     */
    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
