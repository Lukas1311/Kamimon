package de.uniks.stpmon.k.models.builder;

import dagger.internal.Preconditions;
import de.uniks.stpmon.k.constants.TileMapConstants;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class TileLayerBuilder {

    public static TileLayerBuilder builder() {
        return new TileLayerBuilder();
    }

    public static TileLayerBuilder builderObjects() {
        return new TileLayerBuilder()
                .setType(TileMapConstants.OBJECT_LAYER);
    }

    public static TileLayerBuilder builderTiles() {
        return new TileLayerBuilder()
                .setType(TileMapConstants.TILE_LAYER);
    }

    public static TileLayerBuilder builder(TileLayerData data) {
        return builder()
                .setId(data.id())
                .setName(data.name())
                .setX(data.x())
                .setY(data.y())
                .setWidth(data.width())
                .setHeight(data.height())
                .setStartX(data.startx())
                .setStartY(data.starty())
                .setType(data.type())
                .setChunks(data.chunks())
                .setData(data.data())
                .setObjects(data.objects())
                .setProperties(data.properties());
    }

    private List<ChunkData> chunks = null;
    private List<Long> data = null;
    private List<ObjectData> objects = null;
    private final List<Property> properties = new LinkedList<>();
    private int id;
    private String name;
    private int x;
    private int y;
    private int width;
    private int height;
    private int startX;
    private int startY;
    private String type = TileMapConstants.TILE_LAYER;
    private TileMapBuilder owner;

    private TileLayerBuilder() {
    }

    TileLayerBuilder(TileMapBuilder owner) {
        this.owner = owner;
    }

    public TileLayerBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public TileLayerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TileLayerBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public TileLayerBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public TileLayerBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public TileLayerBuilder setX(int x) {
        this.x = x;
        return this;
    }

    public TileLayerBuilder setY(int y) {
        this.y = y;
        return this;
    }

    public TileLayerBuilder setStartX(int startX) {
        this.startX = startX;
        return this;
    }

    public TileLayerBuilder setStartY(int startY) {
        this.startY = startY;
        return this;
    }

    public TileLayerBuilder addChunk(ChunkData chunk) {
        if (this.chunks == null) {
            this.chunks = new LinkedList<>();
        }
        this.chunks.add(chunk);
        return this;
    }

    public TileLayerBuilder addData(long data) {
        if (this.data == null) {
            this.data = new LinkedList<>();
        }
        this.data.add(data);
        return this;
    }

    public TileLayerBuilder addProperty(Property property) {
        this.properties.add(property);
        return this;
    }

    public TileLayerBuilder addProperty(String key, String type, String value) {
        return addProperty(new Property(key, type, value));
    }

    public TileLayerBuilder addObject(ObjectData object) {
        if (this.objects == null) {
            this.objects = new LinkedList<>();
        }
        this.objects.add(object);
        return this;
    }

    public ObjectBuilder startObject() {
        return new ObjectBuilder(this);
    }

    public TileLayerBuilder setProperties(List<Property> properties) {
        this.properties.clear();
        this.properties.addAll(properties);
        return this;
    }

    public TileLayerBuilder setChunks(List<ChunkData> chunks) {
        this.chunks = chunks;
        return this;
    }

    public TileLayerBuilder setData(List<Long> data) {
        this.data = data;
        return this;
    }

    public TileLayerBuilder setObjects(List<ObjectData> objects) {
        this.objects = objects;
        return this;
    }

    public TileMapBuilder endLayer() {
        Preconditions.checkNotNull(owner, "Owner is null, builder was not created by a TileMapBuilder!");
        return owner.addLayer(create());
    }


    public TileLayerData create() {
        return new TileLayerData(
                id,
                name,
                chunks,
                data,
                objects,
                x,
                y,
                width,
                height,
                startX,
                startY,
                type,
                properties
        );
    }

}
