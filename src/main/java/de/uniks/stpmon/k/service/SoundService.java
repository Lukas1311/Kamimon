package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.utils.SoundUtils;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import static javafx.scene.media.MediaPlayer.Status;

@Singleton
public class SoundService {

    @Inject
    SettingsService settingsService;

    protected CompositeDisposable disposables = new CompositeDisposable();

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
        
        disposables.add(settingsService.onSoundValue().subscribe(
                // updates whenever sound value changes
                val -> volumeProperty.set(val / 100),
                err -> System.err.println("Error in sound value subscription: " + err.getMessage())
        ));
        disposables.add(settingsService.onSoundMuted().subscribe(
                muteProperty::set,
                err -> System.err.println("Error in sound muted subscription: " + err.getMessage())
        ));
    }

    private void startPlayer() {
        playlist = SoundUtils.loadAudioFiles();
        playNext();
    }

    private void playNext() {
        if (mediaPlayer != null) {
            stop();
        }

        if (currentMediaIndex < playlist.size()) {
            Media media = playlist.get(currentMediaIndex);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.volumeProperty().bind(volumeProperty);
            mediaPlayer.muteProperty().bind(muteProperty);

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

    public void playOrPause() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.getStatus() == Status.PLAYING) {
            pause();
        }
        if (mediaPlayer.getStatus() == Status.PAUSED) {
            play();
        }
    }

    private void play() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.getStatus() == Status.PLAYING) {
            return;
        }
        mediaPlayer.play();
    }

    private void pause() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.getStatus() == Status.PAUSED) {
            return;
        }
        mediaPlayer.pause();
    }

    public void next() {
        currentMediaIndex++;
        playNext();
    }

    public void previous() {
        currentMediaIndex--;
        if (currentMediaIndex < 0) {
            currentMediaIndex = playlist.size() - 1;
        }
        playNext();
    }

    public void shuffle() {
        if (playlist == null || playlist.isEmpty()) {
            return;
        }
        Collections.shuffle(playlist, new Random());
        currentMediaIndex=0;
        playNext();
    }

    private void stop() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.getStatus() == Status.STOPPED) {
            return;
        }
        mediaPlayer.stop();
    }

    public void destroy() {
        disposables.dispose();
        disposables = new CompositeDisposable();
        mediaPlayer.dispose();
        if (mediaPlayer.getStatus() == Status.DISPOSED) {
            mediaPlayer = null;
        }
    }
}
