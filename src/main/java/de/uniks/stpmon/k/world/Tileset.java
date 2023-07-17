package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.Tile;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.TilesetSource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public record Tileset(
        TilesetSource source,
        TilesetData data,
        BufferedImage image,
        BitSet tallGrassData) {

    public void drawTile(Graphics2D graphics, int x, int y, int index,
                         boolean flippedHorizontally, boolean flippedVertically, boolean flippedDiagonally) {
        int value = data.tilewidth() * (index - source.firstgid());
        int posX = value % data.imagewidth();
        int posY = (value / data.imagewidth()) * data.tileheight();

        int imageX = x * data.tilewidth();
        int imageY = y * data.tileheight();
        int imageWidth = data.tilewidth();
        int imageHeight = data.tileheight();
        if (flippedHorizontally) {
            imageX += data.tilewidth();
            imageWidth *= -1;
        }
        if (flippedVertically) {
            imageY += data.tileheight();
            imageHeight *= -1;
        }

        graphics.drawImage(image.getSubimage(posX, posY, data.tilewidth(), data.tileheight()),
                imageX, imageY, imageWidth, imageHeight, null);
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
            BitSet tallGrassData = new BitSet(data.tilecount());
            boolean anyTallGrass = false;
            if (data.tiles() != null) {
                for (Tile tile : data.tiles()) {
                    for (Property property : tile.properties()) {
                        if (property.name().equals("TallGrass")) {
                            boolean walkable = Boolean.parseBoolean(property.value());
                            tallGrassData.set(tile.id(), walkable);
                            anyTallGrass |= walkable;
                        }
                    }
                }
            }
            return new Tileset(source, data, image,
                    anyTallGrass ? tallGrassData : null);
        }

    }

}
