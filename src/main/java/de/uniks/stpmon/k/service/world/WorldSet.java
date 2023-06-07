package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.models.map.TileProp;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public record WorldSet(
        BufferedImage groundImage,
        BufferedImage propsImage,
        List<TileProp> props,
        Map<String, CharacterSet> characters) {
}
