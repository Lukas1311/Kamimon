package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.utils.SoundUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

@Singleton
public class SoundService {

    @Inject
    SettingsService settingsService;

    private MediaPlayer mediaPlayer;

    @Inject
    public SoundService() {
        
    }

    public void init() {
        setSound();
    }

    public void setSound() {

        //System.out.println(getClass().getPackageName());
        Media banger = SoundUtils.loadAudioFile("whatabanger");
        mediaPlayer = new MediaPlayer(banger);
        setVolume(settingsService.getSoundValue());
    }

    public void play() {
        setVolume(settingsService.getSoundValue());
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

    public void setVolume(double volume) {
        System.out.println("SoundService vol: " + volume);
        mediaPlayer.setVolume(volume);
    }

}
