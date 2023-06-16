package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.*;

public class TileMap {
    private final Map<TilesetSource, Tileset> tilesetBySource;
    private final TileMapData data;
    private final Set<TileLayerData> floorLayers;
    private final Set<TileLayerData> decorationLayers;
    private final List<DecorationLayer> decorations;
    private final int tileWidth;
    private final int tileHeight;
    private final int width;
    private final int height;
    private final List<BufferedImage> layers = new ArrayList<>();

    public TileMap(IMapProvider provider, Map<TilesetSource, Tileset> tilesetBySource) {
        this.data = provider.map();
        this.tilesetBySource = tilesetBySource;
        this.tileHeight = data.tileheight();
        this.tileWidth = data.tilewidth();
        int width = 0;
        int height = 0;

        this.floorLayers = new LinkedHashSet<>();
        this.decorationLayers = new LinkedHashSet<>();
        this.decorations = new LinkedList<>();
        List<TileLayerData> layerData = data.layers();
        for (TileLayerData layer : layerData) {
            width = Math.max(width, layer.width());
            height = Math.max(width, layer.height());
            if (layer.chunks() == null) {
                continue;
            }
            if (layer.name().equals(TileLayerData.GROUND_TYPE) || layer.name().equals(TileLayerData.WALLS_TYPE)) {
                floorLayers.add(layer);
            } else {
                decorationLayers.add(layer);
            }

        }
        this.width = width;
        this.height = height;
    }

    public TileMapData getData() {
        return data;
    }

    public Map<TilesetSource, Tileset> getTilesets() {
        return tilesetBySource;
    }

    public BufferedImage renderMap() {
        return renderMap(width, height);
    }

    public BufferedImage renderFloor() {
        if (layers.isEmpty()) {
            renderMap();
        }
        BufferedImage floorImage = createImage(
                width, height);
        Graphics2D floor = floorImage.createGraphics();
        for (TileLayerData layer : floorLayers) {
            if (layer.chunks() == null) {
                continue;
            }
            BufferedImage layerImage = renderLayer(layer);
            floor.drawImage(layerImage, layer.startx(), layer.starty(), null);
        }
        floor.dispose();
        return floorImage;
    }

    public List<DecorationLayer> renderDecorations() {
        if (layers.isEmpty()) {
            renderMap();
        }
        return decorations;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private BufferedImage createImage(int width, int height) {
        return new BufferedImage(
                width * tileWidth, height * tileHeight,
                BufferedImage.TYPE_4BYTE_ABGR);
    }

    public BufferedImage renderMap(int width, int height) {
        BufferedImage mergedImage = createImage(
                width, height);
        Graphics2D graphics = mergedImage.createGraphics();
        List<TileLayerData> layersed = data.layers();
        for (int i = 0; i < layersed.size(); i++) {
            TileLayerData layer = layersed.get(i);
            if (layer.chunks() == null) {
                continue;
            }
            BufferedImage layerImage = renderLayer(layer);
            graphics.drawImage(layerImage, layer.startx(), layer.starty(), null);
            layers.add(layerImage);

            if (decorationLayers.contains(layer)) {
                decorations.add(new DecorationLayer(layer, i, layerImage));
            }
        }
        graphics.dispose();
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
