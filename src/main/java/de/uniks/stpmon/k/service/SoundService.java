package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.utils.SoundUtils;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.List;

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
    private List<Media> playlist = new ArrayList<>();
    private int currentMediaIndex = 0;
    private boolean repeat = true;

    private DoubleProperty volumeProperty = new SimpleDoubleProperty(1.0); // 1.0 = 100% volume
    private BooleanProperty muteProperty = new SimpleBooleanProperty(false);


    @Inject
    public SoundService() {

    }

    public void init() {
        startPlayer();
        mediaPlayer.volumeProperty().bind(volumeProperty);
        mediaPlayer.muteProperty().bind(muteProperty);
        
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

    private void startPlayer() {
        playlist = SoundUtils.loadAudioFiles();
        if (shuffleProperty.get()) {
            shuffle();
            currentMediaIndex=0;
        }
        playNext();
    }

    private void playNext() {
        if (mediaPlayer != null) {
            stop();
        }

        if (currentMediaIndex < playlist.size()) {
            Media media = playlist.get(currentMediaIndex);
            mediaPlayer = new MediaPlayer(media);

            // end of media event handler is switching to next song
            mediaPlayer.setOnEndOfMedia(() -> {
                currentMediaIndex++;
                playNext();
            });

            play();
        } else {
            // playlist is finished
            if (repeat) {
                currentMediaIndex = 0;
                playNext();
            }
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

    public void destroy() {
        // dispose the subscription
        if (soundValueSub != null && !soundValueSub.isDisposed()) {
            soundValueSub.dispose();
        }
    }
}
