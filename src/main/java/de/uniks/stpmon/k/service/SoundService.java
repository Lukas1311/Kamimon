package de.uniks.stpmon.k.service;

import javax.inject.Singleton;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

@Singleton
public class SoundService {


    Media banger = new Media(getClass().getResource("/sound/whatabanger.mp3").toExternalForm());
    
    MediaPlayer mediaPlayer = new MediaPlayer(banger);

    public void play() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            return;
        }
        mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)) {
            return;
        }
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
            return;
        }
        mediaPlayer.stop();
    }
}
