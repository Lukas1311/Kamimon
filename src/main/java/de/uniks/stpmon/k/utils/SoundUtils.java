package de.uniks.stpmon.k.utils;

import java.util.Objects;

import de.uniks.stpmon.k.Main;
import javafx.scene.media.Media;

public class SoundUtils {
    
    public static Media loadAudioFile(String filename) {
        return new Media(Objects.requireNonNull(Main.class.getResource("sound/" + filename + ".mp3")).toExternalForm());
    }
}
