package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.TilesetSource;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Represents a tileset. A tileset is a collection of tiles that can be used to draw a map.
 *
 * @param data   The data object of the tileset, containing information about the tiles
 *               (e.g. the size of the tiles, properties of the tiles, etc.)
 * @param image  The image of the tileset, containing all tiles
 * @param source The source of the tileset, containing information about the location of the tileset
 * @see <a href="https://doc.mapeditor.org/en/stable/reference/json-map-format/#tileset">Tileset</a>
 */
public record Tileset(
        TilesetSource source,
        TilesetData data,
        BufferedImage image) {

    /**
     * Draw a tile on the given graphics context on the specified position.
     * All three flip flags are supported at the same time. They are contained in the global id of the tile in the
     * chunk or layer data.
     *
     * @param graphics            The graphics context to draw the tile on
     * @param x                   The x position of the tile
     * @param y                   The y position of the tile
     * @param index               The index of the tile in the tileset (the id of the tile)
     * @param flippedHorizontally Whether the tile should be flipped horizontally
     * @param flippedVertically   Whether the tile should be flipped vertically
     * @param flippedDiagonally   Whether the tile should be flipped diagonally
     */
    public void drawTile(Graphics2D graphics, int x, int y, int index,
                         boolean flippedHorizontally, boolean flippedVertically, boolean flippedDiagonally) {
        int value = data.tilewidth() * (index - source.firstgid());
        int posX = value % data.imagewidth();
        int posY = (value / data.imagewidth()) * data.tileheight();

        int imageX = x * data.tilewidth();
        int imageY = y * data.tileheight();
        int imageWidth = data.tilewidth();
        int imageHeight = data.tileheight();
        applyTransform(graphics, flippedHorizontally, flippedVertically, flippedDiagonally, imageX, imageY);
        graphics.drawImage(image.getSubimage(posX, posY, data.tilewidth(), data.tileheight()),
                -(int) Math.ceil(data.tilewidth() / 2.0f), -(int) Math.ceil(data.tileheight() / 2.0f), imageWidth, imageHeight, null);
    }

    /**
     * Applies the transformation to the graphics object. This is used to flip the image vertically, horizontally or
     * diagonally. All three transformations can be applied at the same time.
     *
     * @param graphics            The graphics object to apply the transformation to
     * @param flippedHorizontally Whether the image should be flipped horizontally
     * @param flippedVertically   Whether the image should be flipped vertically
     * @param flippedDiagonally   Whether the image should be flipped diagonally
     * @param imageX              The x position of the image
     * @param imageY              The y position of the image
     * @see <a href="https://github.com/mapeditor/tiled/blob/master/src/libtiled/maprenderer.cpp">
     * Original Tiled rending</a>
     */
    private void applyTransform(Graphics2D graphics,
                                boolean flippedHorizontally, boolean flippedVertically, boolean flippedDiagonally,
                                int imageX, int imageY) {
        int rotation = 0;
        boolean horizontally = flippedHorizontally;
        boolean vertically = flippedVertically;
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        // Flips the image diagonally, swapping the x and y-axis
        if (flippedDiagonally) {
            rotation = 1;

            horizontally = vertically;
            vertically = !flippedHorizontally;
        }
        // Flips the image horizontally
        if (horizontally) {
            scaleX *= -1.0f;
        }
        // Flips the image vertically
        if (vertically) {
            scaleY *= -1.0f;
        }

        AffineTransform transform = new AffineTransform();
        transform.translate(imageX + Math.ceil(data.tilewidth() / 2.0), imageY + Math.ceil(data.tileheight() / 2.0));
        transform.quadrantRotate(rotation);
        transform.scale(scaleX, scaleY);
        graphics.setTransform(transform);
    }

    public static Tileset.Builder builder() {
        return new Tileset.Builder();
    }

    public static class Builder {

        private TilesetSource source;
        private TilesetData data;
        private BufferedImage image;


        private Builder() {
        }

        public void setData(TilesetData data) {
            this.data = data;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        public Builder setSource(TilesetSource source) {
            this.source = source;
            return this;
        }

        public Tileset build() {
            return new Tileset(source, data, image);
        }

    }

}
