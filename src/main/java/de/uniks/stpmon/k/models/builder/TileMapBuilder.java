package de.uniks.stpmon.k.models.builder;

import de.uniks.stpmon.k.constants.TileConstants;
import de.uniks.stpmon.k.constants.TileMapConstants;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class TileMapBuilder {

    public static TileMapBuilder builder() {
        return new TileMapBuilder();
    }

    public static TileMapBuilder builder(TileMapData data) {
        return builder()
                .setWidth(data.width())
                .setHeight(data.height())
                .setInfinite(data.infinite())
                .setLayers(data.layers())
                .setProperties(data.properties())
                .setTileWidth(data.tilewidth())
                .setTileHeight(data.tileheight())
                .setTileSets(data.tilesets())
                .setType(data.type());
    }

    private final List<TileLayerData> layers = new LinkedList<>();
    private final List<Property> properties = new LinkedList<>();
    private final List<TilesetSource> tileSets = new LinkedList<>();
    private int width;
    private int height;
    private boolean infinite;
    private int tileWidth = TileConstants.TILE_SIZE;
    private int tileHeight = TileConstants.TILE_SIZE;
    private String type = "map";

    public TileMapBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public TileMapBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public TileMapBuilder setInfinite(boolean infinite) {
        this.infinite = infinite;
        return this;
    }

    public TileMapBuilder setLayers(List<TileLayerData> layers) {
        this.layers.clear();
        this.layers.addAll(layers);
        return this;
    }

    public TileMapBuilder addLayer(TileLayerData layer) {
        this.layers.add(layer);
        return this;
    }

    public TileLayerBuilder startLayer() {
        return new TileLayerBuilder(this);
    }

    public TileLayerBuilder startObjectLayer() {
        return new TileLayerBuilder(this)
                .setType(TileMapConstants.OBJECT_LAYER);
    }

    public TileLayerBuilder startTileLayer() {
        return new TileLayerBuilder(this)
                .setType(TileMapConstants.TILE_LAYER);
    }

    public TileMapBuilder setProperties(List<Property> properties) {
        this.properties.clear();
        this.properties.addAll(properties);
        return this;
    }

    public TileMapBuilder addProperty(Property property) {
        this.properties.add(property);
        return this;
    }

    public TileMapBuilder addProperty(String key, String type, String value) {
        return addProperty(new Property(key, type, value));
    }

    public TileMapBuilder setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
        return this;
    }

    public TileMapBuilder setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
        return this;
    }

    public TileMapBuilder setTileSets(List<TilesetSource> tileSets) {
        this.tileSets.clear();
        this.tileSets.addAll(tileSets);
        return this;
    }

    public TileMapBuilder addTileSet(TilesetSource tileSet) {
        this.tileSets.add(tileSet);
        return this;
    }

    public TileMapBuilder addTileSet(int firstGid, String source) {
        return addTileSet(new TilesetSource(firstGid, source));
    }

    public TileMapBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public TileMapData create() {
        if (width == 0) {
            width = layers.stream().mapToInt(TileLayerData::width).max().orElse(0);
        }
        if (height == 0) {
            height = layers.stream().mapToInt(TileLayerData::width).max().orElse(0);
        }
        return new TileMapData(width, height, infinite, layers, properties, tileWidth, tileHeight, tileSets, type);
    }
}
