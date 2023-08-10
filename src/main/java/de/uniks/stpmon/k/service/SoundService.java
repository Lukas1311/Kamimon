package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.Main;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    @Inject
    EffectContext effectContext;

    // is of form "file:/.../de/uniks/stpmon/k/sound/"
    private static final URL basePathURL = Main.class.getResource("sound/");

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
        if (mediaPlayer == null) {
            return;
        }
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
        playlist = loadAudioFiles();
        if (playlist == null) {
            return;
        }
        mediaPlayer = new MediaPlayer(playlist.get(0));
        playNext();
    }

    private void playNext() {
        if (mediaPlayer != null) {
            stop();
        }

        if (playlist == null) {
            return;
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
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            if (mediaPlayer.getStatus() == Status.DISPOSED) {
                mediaPlayer = null;
            }
        }
    }

    private List<Media> loadAudioFiles() {
        if (effectContext != null && effectContext.shouldSkipLoadAudio()) {
            return null;
        }

        List<Media> mediaFiles = new ArrayList<>();
        Path basePath = null;
        try {
            assert basePathURL != null;
            basePath = Paths.get(basePathURL.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Error: " + e.getMessage());
        }

        assert basePath != null;
        File folder = new File(basePath.toString());
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    Media media = loadAudioFile(file);
                    mediaFiles.add(media);
                }
            }
        }

        return mediaFiles;
    }

    private Media loadAudioFile(File file) {
        if (effectContext != null && effectContext.shouldSkipLoadAudio()) {
            return null;
        }
        if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
            return loadAudioFile(file.toURI().toString());
        }
        return null;
    }

    private Media loadAudioFile(String filename) {
        if (effectContext != null && effectContext.shouldSkipLoadAudio()) {
            return null;
        }
        try {
            URI uri = new URI(filename);
            if (uri.isAbsolute()
                    && uri.toString().startsWith(Objects.requireNonNull(basePathURL).toURI().toString())
                    && uri.toString().toLowerCase().endsWith(".mp3")
            ) {
                return new Media(Objects.requireNonNull(filename));
            } else {
                return new Media(Objects.requireNonNull(Main.class.getResource(basePathURL + filename + ".mp3")).toExternalForm());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
        }
        return null;
    }
}
