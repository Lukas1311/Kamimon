package de.uniks.stpmon.k.utils;

import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.TilesetSource;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public record Tileset(TilesetSource source,
                      TilesetData data,
                      BufferedImage image) {

    public void setTile(WritableRaster raster, int x, int y, int index) {
        int value = data.tilewidth() * (index - source.firstgid());
        int posX = value % data.imagewidth();
        int posY = (value / data.imagewidth()) * data.tileheight();
        ImageUtils.copyData(raster, image,
                x * data.tilewidth(), y * data.tileheight(),
                posX, posY,
                data.tilewidth(), data.tileheight());
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

        public Builder setData(TilesetData data) {
            this.data = data;
            return this;
        }

        public Builder setImage(BufferedImage image) {
            this.image = image;
            return this;
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
