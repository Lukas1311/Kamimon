package de.uniks.stpmon.k.images;

import de.uniks.stpmon.k.dto.map.Chunk;
import de.uniks.stpmon.k.dto.map.TileLayer;
import de.uniks.stpmon.k.dto.map.TileMap;
import de.uniks.stpmon.k.dto.map.Tileset;
import de.uniks.stpmon.k.dto.map.TilesetSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static de.uniks.stpmon.k.images.ImageUtils.TILE_SIZE;

public class TiledImage {
    public final Map<TilesetSource, Tileset> tilesets;
    public final Map<TilesetSource, BufferedImage> images;

    public TiledImage(Map<TilesetSource, Tileset> tilesets, Map<TilesetSource, BufferedImage> images) {
        this.tilesets = tilesets;
        this.images = images;
    }

    public BufferedImage renderMap(TileMap tileMap) throws IOException {
        int width = tileMap.layers().get(0).width();
        int height = tileMap.layers().get(0).height();
        return renderMap(tileMap, width, height);
    }

    public BufferedImage renderMap(TileMap tileMap, int width, int height) throws IOException {
        BufferedImage mergedImage = ImageUtils.createImage(width, height);
        Graphics2D g = mergedImage.createGraphics();
        int i = 0;
        for (TileLayer layer : tileMap.layers()) {
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

    public BufferedImage renderLayer(TileLayer layer) {
        int width = layer.width();
        int height = layer.height();
        BufferedImage chunkImage = ImageUtils.createImage(width, height);
        for (Chunk chunk : layer.chunks()) {
            BufferedImage chunkImage1 = renderChunk(chunk);
            ImageUtils.copyData(chunkImage.getRaster(), chunkImage1,
                    chunk.x() * TILE_SIZE, chunk.y() * TILE_SIZE,
                    0, 0,
                    chunk.width() * TILE_SIZE, chunk.height() * TILE_SIZE);
        }
        return chunkImage;
    }

    public BufferedImage renderChunk(Chunk chunk) {
        BufferedImage chunkImage = ImageUtils.createImage(chunk.width(), chunk.height());
        WritableRaster raster = chunkImage.getRaster();

        for (int x = 0; x < chunk.width(); x++) {
            for (int y = 0; y < chunk.height(); y++) {
                int data = chunk.data().get(x + y * chunk.height());
                if (data == 0) {
                    continue;
                }
                TilesetSource source = getSource(data);
                Tileset tileset = tilesets.get(source);
                int value = tileset.tilewidth() * (data - source.firstgid());
                int posX = value % tileset.imagewidth();
                int posY = (value / tileset.imagewidth()) * tileset.tileheight();
                ImageUtils.copyData(raster,
                        images.get(source),
                        x * tileset.tilewidth(), y * tileset.tileheight(),
                        posX, posY,
                        tileset.tilewidth(), tileset.tileheight());
            }
        }
        return chunkImage;
    }

    private TilesetSource getSource(int gid) {
        return tilesets.keySet().stream().filter(tileset -> tileset.firstgid() <= gid).findFirst().orElse(null);
    }

}
