package de.uniks.stpmon.k.images;

import de.uniks.stpmon.k.dto.map.ChunkData;
import de.uniks.stpmon.k.dto.map.TileLayerData;
import de.uniks.stpmon.k.dto.map.TileMapData;
import de.uniks.stpmon.k.dto.map.TilesetData;
import de.uniks.stpmon.k.dto.map.TilesetSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static de.uniks.stpmon.k.images.ImageUtils.TILE_SIZE;

public class TileMap {
    private final Map<TilesetSource, Tileset> tilesetBySource;
    private final TileMapData data;

    public TileMap(TileMapData data, Map<TilesetSource, Tileset> tilesetBySource) {
        this.data = data;
        this.tilesetBySource = tilesetBySource;
    }

    public TileMapData getData() {
        return data;
    }

    public Map<TilesetSource, Tileset> getTilesets() {
        return tilesetBySource;
    }

    public Tileset getTileset(TilesetSource source) {
        return tilesetBySource.get(source);
    }

    public Optional<Tileset> getTileset(String filename) {
        return tilesetBySource.values().stream()
                .filter(tileset -> tileset.source().source().equals(filename))
                .findFirst();
    }

    public BufferedImage renderMap() throws IOException {
        int width = data.layers().get(0).width();
        int height = data.layers().get(0).height();
        return renderMap(width, height);
    }

    public BufferedImage renderMap(int width, int height) throws IOException {
        BufferedImage mergedImage = ImageUtils.createImage(width, height);
        Graphics2D g = mergedImage.createGraphics();
        int i = 0;
        for (TileLayerData layer : data.layers()) {
            if (layer.chunks() == null) {
                continue;
            }
            BufferedImage layerImage = renderLayer(layer);
            ImageIO.write(layerImage, "png", new File("test_layer_" + i++ + ".png"));
            g.drawImage(layerImage, 0, 0, null);
        }
        g.dispose();
        return mergedImage;
    }

    public BufferedImage renderLayer(TileLayerData layer) {
        int width = layer.width();
        int height = layer.height();
        BufferedImage chunkImage = ImageUtils.createImage(width, height);
        for (ChunkData chunk : layer.chunks()) {
            BufferedImage chunkImage1 = renderChunk(chunk);
            ImageUtils.copyData(chunkImage.getRaster(), chunkImage1,
                    chunk.x() * TILE_SIZE, chunk.y() * TILE_SIZE,
                    0, 0,
                    chunk.width() * TILE_SIZE, chunk.height() * TILE_SIZE);
        }
        return chunkImage;
    }

    public BufferedImage renderChunk(ChunkData chunk) {
        BufferedImage chunkImage = ImageUtils.createImage(chunk.width(), chunk.height());
        WritableRaster raster = chunkImage.getRaster();

        for (int x = 0; x < chunk.width(); x++) {
            for (int y = 0; y < chunk.height(); y++) {
                int data = chunk.data().get(x + y * chunk.height());
                if (data == 0) {
                    continue;
                }
                TilesetSource source = getSource(data);
                Tileset tileset = tilesetBySource.get(source);
                TilesetData tilesetData = tileset.data();
                int value = tilesetData.tilewidth() * (data - source.firstgid());
                int posX = value % tilesetData.imagewidth();
                int posY = (value / tilesetData.imagewidth()) * tilesetData.tileheight();
                ImageUtils.copyData(raster,
                        tileset.image(),
                        x * tilesetData.tilewidth(), y * tilesetData.tileheight(),
                        posX, posY,
                        tilesetData.tilewidth(), tilesetData.tileheight());
            }
        }
        return chunkImage;
    }

    private TilesetSource getSource(int gid) {
        return tilesetBySource.keySet().stream().filter(tileset -> tileset.firstgid() <= gid).findFirst().orElse(null);
    }

}
