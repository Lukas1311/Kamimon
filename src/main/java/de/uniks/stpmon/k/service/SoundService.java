package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.Main;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unused")
@Singleton
public class SoundService {
    private final static boolean REPEAT = true;

    @Inject
    SettingsService settingsService;
    @Inject
    EffectContext effectContext;

    String[] songNames = {
        "01_Pokémon_Gym",
        "02_Route_3",
        "03_Azalea_Town",
        "04_Hauoli_City",
        "05_Ecruteak_City",
        "06_Route_30",
        "07_National_Park",
        "08_Pokémon_League",
        "09_Anistar_City",
        "10_Relic_Song",
        "11_Sacred_Beasts",
        "12_Route_113",
        "13_Trainer_Battle",
        "14_Lavender_Town",
        "15_Ending_Theme",
        "16_Title_Screen"
    };

    // is of form "file:/.../de/uniks/stpmon/k/sound/"
    private static final URL basePathURL = Main.class.getResource("sound/");

    protected CompositeDisposable disposables = new CompositeDisposable();

    private MediaPlayer mediaPlayer;

    private final List<Media> playlist = new ArrayList<>();

    private final DoubleProperty volumeProperty = new SimpleDoubleProperty(1.0); // 1.0 = 100% volume
    private final BooleanProperty muteProperty = new SimpleBooleanProperty(false);
    private final IntegerProperty currentSongProperty = new SimpleIntegerProperty(0);


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

        listen(currentSongProperty,
                (observable, oldValue, newValue) -> settingsService.setCurrentSong(newValue.intValue()));

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
        for (String song : songNames) {
            Media media = loadAudioFile(song);
            if (media != null) {
                playlist.add(media);
            }   
        }
        if (playlist.isEmpty()) {
            return;
        }
        playNext();
    }

    private void playNext() {
        if (mediaPlayer != null) {
            stop();
        }

        if (currentSongProperty.get() < playlist.size()) {
            Media media = playlist.get(currentSongProperty.get());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.volumeProperty().bind(volumeProperty);
            mediaPlayer.muteProperty().bind(muteProperty);

            // end of media event handler is switching to next song
            mediaPlayer.setOnEndOfMedia(() -> {
                int currentIndex = currentSongProperty.get();
                currentSongProperty.set(currentIndex + 1);
                playNext();
            });

            play();
        } else {
            // playlist is finished
            if (REPEAT) {
                currentSongProperty.set(0);
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

    public void loopSong(String filename) {
        Media chosenSong = loadAudioFile(filename);
        if (chosenSong == null) {
            return;
        }
        mediaPlayer = new MediaPlayer(chosenSong);
        mediaPlayer.volumeProperty().bind(volumeProperty);
        mediaPlayer.muteProperty().bind(muteProperty);

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        });

        play();
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
        int currentIndex = currentSongProperty.get();
        currentSongProperty.set(currentIndex + 1);
        playNext();
    }

    public void previous() {
        int currentIndex = currentSongProperty.get();
        currentSongProperty.subtract(currentIndex - 1);
        if (currentSongProperty.get() < 0) {
            currentSongProperty.set(playlist.size() - 1);
        }
        playNext();
    }

    public void shuffle() {
        if (playlist.isEmpty()) {
            return;
        }
        Collections.shuffle(playlist, new Random());
        currentSongProperty.set(0);
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
                return new Media(Objects.requireNonNull(Main.class.getResource("sound/" + filename + ".mp3")).toExternalForm());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
        }
        return null;
    }

    protected <T> void listen(ObservableValue<T> property, ChangeListener<? super T> listener) {
        property.addListener(listener);
        onDestroy(() -> property.removeListener(listener));
    }

    private void onDestroy(Runnable action) {
        disposables.add(Disposable.fromRunnable(action));
    }
}
