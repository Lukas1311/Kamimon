package de.uniks.stpmon.k.utils;

import java.awt.image.BufferedImage;
import java.util.List;

public record World(
        BufferedImage groundImage,
        BufferedImage propsImage,
        List<TileProp> props) {
}
