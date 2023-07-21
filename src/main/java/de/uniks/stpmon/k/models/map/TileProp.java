package de.uniks.stpmon.k.models.map;

import java.awt.image.BufferedImage;

/**
 * Represents a tile prop (e.g. a tree) on the map. The prop is rendered on the map
 * at the given position and with the given size. The sort layer determines the order
 * in which the props are rendered. Props with a lower sort layer are rendered first.
 * <p>
 * The props are extracted from the tilemap image layers.
 *
 * @param image     The image of the prop
 * @param x         The x position of the prop
 * @param y         The y position of the prop
 * @param width     The width of the prop
 * @param height    The height of the prop
 * @param sortLayer The sort layer of the prop (lower values are rendered first)
 */
public record TileProp(
        BufferedImage image,
        int x,
        int y,
        int width,
        int height, int sortLayer) {

}
