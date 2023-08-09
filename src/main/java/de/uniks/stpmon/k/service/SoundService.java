package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.utils.SoundUtils;
import io.reactivex.rxjava3.disposables.Disposable;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

@Singleton
public class SoundService {

    @Inject
    SettingsService settingsService;

    private MediaPlayer mediaPlayer;
    private Disposable soundValueSub;
    private Disposable soundMutedSub;

    private DoubleProperty volumeProperty = new SimpleDoubleProperty(1.0); // 1.0 = 100% volume
    private BooleanProperty muteProperty = new SimpleBooleanProperty(false);


    @Inject
    public SoundService() {

    }

    public void init() {
        setSound();
        soundValueSub = settingsService.onSoundValue().subscribe(
                // updates whenever sound value changes
                val -> volumeProperty.set(val / 100),
                err -> System.err.println("Error in sound value subscription: " + err.getMessage())

        );
        soundMutedSub = settingsService.onSoundMuted().subscribe(
                muteProperty::set,
                err -> System.err.println("Error in sound muted subscription: " + err.getMessage())
        );
    }

    private void setSound() {
        Media banger = SoundUtils.loadAudioFile("whatabanger");
        mediaPlayer = new MediaPlayer(banger);
    }

    public void play() {

        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                return;
            }
            mediaPlayer.play();
        } else {
            setSound();
            mediaPlayer.play();
        }
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

    // public void setVolume(double volume) {
    //     mediaPlayer.setVolume(volume);
    // }

    // public void mute(boolean flag) {
    //     mediaPlayer.setMute(flag);
    // }

    public void destroy() {
        // dispose the subscription
        if (soundValueSub != null && !soundValueSub.isDisposed()) {
            soundValueSub.dispose();
        }
    }
}
