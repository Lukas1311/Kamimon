package de.uniks.stpmon.k.models.map;

import java.awt.image.BufferedImage;

public record RegionImage(String id, BufferedImage defaultImage, BufferedImage grayscaleImage) {

}