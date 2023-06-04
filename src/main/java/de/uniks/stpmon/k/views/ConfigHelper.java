package de.uniks.stpmon.k.views;

import java.io.*;

/**
 * This class provides methods for saving and retrieving the sprite index from a configuration file (config.txt)
 */
public class ConfigHelper {
    private static final String CONFIG_FILE_PATH = "config.txt";

    public static void saveSpriteIndex(int index) {
        // save the index value to the configuration file
        try (PrintWriter writer = new PrintWriter(CONFIG_FILE_PATH)) {
            writer.println(index);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getSpriteIndex() {
        // read the value from the configuration file
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE_PATH))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}