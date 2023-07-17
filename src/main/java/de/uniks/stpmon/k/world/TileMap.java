package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.constants.TileConstants;
import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.ITileDataProvider;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class TileMap {

    private final Map<TilesetSource, Tileset> tilesetBySource;
    private final TileMapData data;
    private final IMapProvider provider;
    private final Set<TileLayerData> floorLayers;
    private final Set<TileLayerData> decorationLayers;
    private final List<DecorationLayer> decorations;
    private final int tileWidth;
    private final int tileHeight;
    private final int width;
    private final int height;
    private final Map<TileLayerData, BufferedImage> layers = new HashMap<>();

    public TileMap(IMapProvider provider, Map<TilesetSource, Tileset> tilesetBySource) {
        this.provider = provider;
        this.data = provider.map();
        this.tilesetBySource = tilesetBySource;
        this.tileHeight = data.tileheight();
        this.tileWidth = data.tilewidth();
        this.floorLayers = new LinkedHashSet<>();
        this.decorationLayers = new LinkedHashSet<>();
        this.decorations = new LinkedList<>();

        int width = 0;
        int height = 0;
        List<TileLayerData> layerData = data.layers();
        for (TileLayerData layer : layerData) {
            width = Math.max(width, layer.width());
            height = Math.max(height, layer.height());
            if (layer.chunks() == null && layer.data() == null) {
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

    public IMapProvider getProvider() {
        return provider;
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
            if (!layers.containsKey(layer)) {
                continue;
            }
            BufferedImage layerImage = layers.get(layer);
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
            if (layer.chunks() == null && layer.data() == null) {
                continue;
            }
            BufferedImage layerImage = renderLayer(layer, width, height);
            graphics.drawImage(layerImage, 0, 0, null);
            layers.put(layer, layerImage);

            if (decorationLayers.contains(layer)) {
                decorations.add(new DecorationLayer(layer, i, layerImage));
            }
        }
        graphics.dispose();
        return mergedImage;
    }

    public BufferedImage renderLayer(TileLayerData layer, int width, int height) {
        if (layer.data() != null && !layer.data().isEmpty()) {
            return renderData(layer);
        }
        BufferedImage layerImage = createImage(width, height);
        for (ChunkData chunk : layer.chunks()) {
            int startX = chunk.x();
            int startY = chunk.y();
            BufferedImage chunkImage1 = renderData(chunk);
            ImageUtils.copyData(layerImage.getRaster(),
                    chunkImage1,
                    startX * tileWidth, startY * tileHeight,
                    0, 0,
                    chunk.width() * tileWidth, chunk.height() * tileHeight);
        }
        return layerImage;
    }

    public BufferedImage renderData(ITileDataProvider provider) {
        BufferedImage chunkImage = createImage(provider.width(), provider.height());
        Graphics2D graphics = chunkImage.createGraphics();

        for (int x = 0; x < provider.width(); x++) {
            for (int y = 0; y < provider.height(); y++) {

                long globalId = provider.getGlobalIdFromLocal(x, y);
                int data = (int) (globalId & TileConstants.TILE_ID_MASK);
                if (data == 0) {
                    continue;
                }
                TilesetSource source = getSource(data);
                Tileset tileset = tilesetBySource.get(source);
                // Index out of bounds for all tile sets
                if ((data - source.firstgid()) > tileset.data().tilecount()) {
                    continue;
                }
                tileset.drawTile(graphics, x, y, data,
                        (globalId & TileConstants.FLAG_FLIPPED_HORIZONTALLY) > 0,
                        (globalId & TileConstants.FLAG_FLIPPED_VERTICALLY) > 0,
                        false);
            }
        }
        graphics.dispose();
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
