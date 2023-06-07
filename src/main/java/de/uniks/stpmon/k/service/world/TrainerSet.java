package de.uniks.stpmon.k.service.world;

import java.awt.image.BufferedImage;

public class TrainerSet {
    private final String name;
    private final BufferedImage image;

    public TrainerSet(String name, BufferedImage image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }
}
