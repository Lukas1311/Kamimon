package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.ChunkData;
import de.uniks.stpmon.k.models.map.TileLayerData;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TileMap {
    private final Map<TilesetSource, Tileset> tilesetBySource;
    private final TileMapData data;
    private final int tileWidth;
    private final int tileHeight;
    private final int width;
    private final int height;
    private final List<BufferedImage> layers = new ArrayList<>();
    private BufferedImage mainImage;

    public TileMap(IMapProvider provider, Map<TilesetSource, Tileset> tilesetBySource) {
        this.data = provider.map();
        this.tilesetBySource = tilesetBySource;
        this.tileHeight = data.tileheight();
        this.tileWidth = data.tilewidth();
        this.width = data.layers().get(0).width();
        this.height = data.layers().get(0).height();
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

    public BufferedImage renderMap() {
        return renderMap(width, height);
    }

    public BufferedImage getMainImage() {
        return mainImage;
    }

    public List<BufferedImage> getLayers() {
        return layers;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private BufferedImage createImage(int width, int height) {
        return new BufferedImage(
                width * tileWidth,
                height * tileHeight,
                BufferedImage.TYPE_4BYTE_ABGR);
    }

    public BufferedImage renderMap(int width, int height) {
        BufferedImage mergedImage = createImage(
                width, height);
        Graphics2D g = mergedImage.createGraphics();
        int i = 0;
        for (TileLayerData layer : data.layers()) {
            if (layer.chunks() == null) {
                continue;
            }
            BufferedImage layerImage = renderLayer(layer);
            g.drawImage(layerImage, layer.startx(), layer.starty(), null);
            layers.add(layerImage);
        }
        g.dispose();
        mainImage = mergedImage;
        return mergedImage;
    }

    public BufferedImage renderLayer(TileLayerData layer) {
        int width = layer.width();
        int height = layer.height();
        BufferedImage chunkImage = createImage(
                width, height);
        for (ChunkData chunk : layer.chunks()) {
            BufferedImage chunkImage1 = renderChunk(chunk);
            ImageUtils.copyData(chunkImage.getRaster(),
                    chunkImage1,
                    chunk.x() * tileWidth, chunk.y() * tileHeight,
                    0, 0,
                    chunk.width() * tileWidth, chunk.height() * tileHeight);
        }
        return chunkImage;
    }

    public BufferedImage renderChunk(ChunkData chunk) {
        BufferedImage chunkImage = createImage(chunk.width(), chunk.height());
        WritableRaster raster = chunkImage.getRaster();

        for (int x = 0; x < chunk.width(); x++) {
            for (int y = 0; y < chunk.height(); y++) {
                int data = chunk.data().get(x + y * chunk.height());
                if (data == 0) {
                    continue;
                }
                TilesetSource source = getSource(data);
                Tileset tileset = tilesetBySource.get(source);
                tileset.setTile(raster, x, y, data);
            }
        }
        return chunkImage;
    }

    private TilesetSource getSource(int gid) {
        return tilesetBySource.keySet().stream()
                .sorted(Comparator.comparingInt(TilesetSource::firstgid).reversed())
                .filter(tileset -> (gid - tileset.firstgid()) >= 0)
                .findFirst()
                .orElse(null);
    }

}
