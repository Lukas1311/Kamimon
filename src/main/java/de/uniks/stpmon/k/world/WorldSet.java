package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.TileProp;

import java.awt.image.BufferedImage;
import java.util.List;

public record WorldSet(BufferedImage groundImage, List<TileProp> props) {
}
