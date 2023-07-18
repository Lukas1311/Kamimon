package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.TilesetSource;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public record Tileset(
        TilesetSource source,
        TilesetData data,
        BufferedImage image) {

    public void drawTile(Graphics2D graphics, int x, int y, int index,
                         boolean flippedHorizontally, boolean flippedVertically, boolean flippedDiagonally) {
        int value = data.tilewidth() * (index - source.firstgid());
        int posX = value % data.imagewidth();
        int posY = (value / data.imagewidth()) * data.tileheight();

        int imageX = x * data.tilewidth();
        int imageY = y * data.tileheight();
        int imageWidth = data.tilewidth();
        int imageHeight = data.tileheight();
        if (flippedHorizontally || flippedDiagonally || flippedVertically) {
            applyTransform(graphics, flippedHorizontally, flippedVertically, flippedDiagonally, imageX, imageY);
        }
        graphics.drawImage(image.getSubimage(posX, posY, data.tilewidth(), data.tileheight()),
                0, 0, imageWidth, imageHeight, null);
    }

    private void applyTransform(Graphics2D graphics,
                                boolean flippedHorizontally, boolean flippedVertically, boolean flippedDiagonally,
                                int imageX, int imageY) {
        int rotation = 0;
        boolean horizontally = flippedHorizontally;
        boolean vertically = flippedVertically;
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        if (flippedDiagonally) {
            rotation = 1;

            horizontally = vertically;
            vertically = !flippedHorizontally;
        }
        if (horizontally) {
            imageX += data.tilewidth();
            scaleX *= -1.0f;
        }
        if (vertically) {
            imageY += data.tileheight();
            scaleY *= -1.0f;
        }

        AffineTransform transform = new AffineTransform();
        transform.translate(imageX, imageY);
        transform.quadrantRotate(rotation, data.tilewidth() / 2.0, data.tileheight() / 2.0);
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
