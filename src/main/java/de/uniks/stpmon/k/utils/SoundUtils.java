package de.uniks.stpmon.k.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.uniks.stpmon.k.Main;
import javafx.scene.media.Media;

public class SoundUtils {

    // is of form "file:/.../de/uniks/stpmon/k/sound/"
    private static final URL basePathURL = Main.class.getResource("sound/");

    public static List<Media> loadAudioFiles() {

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

    public static Media[] loadAudioFiles(String[] filenames) {
        Media[] mediaFiles = new Media[filenames.length];

        for (int i = 0; i < filenames.length; i++) {
            mediaFiles[i] = loadAudioFile(filenames[i]);
        }

        return mediaFiles;
    }
    
    public static Media loadAudioFile(File file) {
        if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
            return loadAudioFile(file.toURI().toString());
        }
        return null;
    }

    public static Media loadAudioFile(String filename) {
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
