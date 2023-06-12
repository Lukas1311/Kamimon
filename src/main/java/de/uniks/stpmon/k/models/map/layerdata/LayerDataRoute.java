package de.uniks.stpmon.k.models.map.layerdata;

import java.util.List;

public record LayerDataRoute(
    int id,
    String name,
    List<ObjectData> objects,
    int x,
    int y,
    int opacity,
    String type,
    boolean visible,
    String draworder,
    List<Property> properties
) implements ITileLayerData {}
