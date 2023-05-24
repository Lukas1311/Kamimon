package de.uniks.stpmon.k.images;

import de.uniks.stpmon.k.dto.map.TilesetData;
import de.uniks.stpmon.k.dto.map.TilesetSource;

import java.awt.image.BufferedImage;

public record Tileset(TilesetSource source,
                      TilesetData data,
                      BufferedImage image) {

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
