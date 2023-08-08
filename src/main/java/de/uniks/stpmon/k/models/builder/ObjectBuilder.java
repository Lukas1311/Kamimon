package de.uniks.stpmon.k.models.builder;

import dagger.internal.Preconditions;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class ObjectBuilder {

    public static ObjectBuilder builder() {
        return new ObjectBuilder();
    }

    public static ObjectBuilder builder(ObjectData data) {
        return builder()
                .setId(data.id())
                .setName(data.name())
                .setType(data.type())
                .setWidth(data.width())
                .setHeight(data.height())
                .setX(data.x())
                .setY(data.y())
                .setPolygon(data.polygon())
                .setProperties(data.properties());
    }

    private List<PolygonPoint> polygon;
    private List<Property> properties;

    private int id;
    private String name = "";
    private String type;
    private int width;
    private int height;
    private int x;
    private int y;

    private TileLayerBuilder owner;

    private ObjectBuilder() {
    }

    ObjectBuilder(TileLayerBuilder owner) {
        this.owner = owner;
    }

    public ObjectBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public ObjectBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ObjectBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ObjectBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public ObjectBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public ObjectBuilder setX(int x) {
        this.x = x;
        return this;
    }

    public ObjectBuilder setY(int y) {
        this.y = y;
        return this;
    }

    public ObjectBuilder setPolygon(List<PolygonPoint> polygon) {
        this.polygon = polygon;
        return this;
    }

    public ObjectBuilder addPolygonPoint(PolygonPoint point) {
        if (polygon == null) {
            polygon = new LinkedList<>();
        }
        this.polygon.add(point);
        return this;
    }

    public ObjectBuilder setProperties(List<Property> properties) {
        this.properties = properties;
        return this;
    }


    public ObjectBuilder addProperty(Property property) {
        if (properties == null) {
            properties = new LinkedList<>();
        }
        this.properties.add(property);
        return this;
    }

    public ObjectBuilder addProperty(String key, String type, String value) {
        return addProperty(new Property(key, type, value));
    }

    public TileLayerBuilder endObject() {
        Preconditions.checkNotNull(owner, "Owner is null, builder was not created by a TileLayerBuilder!");
        return owner.addObject(create());
    }

    public ObjectData create() {
        return new ObjectData(id, name, polygon, properties, type, width, height, x, y);
    }
}
