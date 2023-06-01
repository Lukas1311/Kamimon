package de.uniks.stpmon.k.models.map;

import java.awt.image.BufferedImage;

public record TileProp(
        BufferedImage image,
        int x,
        int y,
        int width,
        int height) {
}
