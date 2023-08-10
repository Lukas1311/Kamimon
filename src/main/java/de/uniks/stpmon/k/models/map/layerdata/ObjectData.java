package de.uniks.stpmon.k.models.map.layerdata;

import de.uniks.stpmon.k.models.map.Property;

import java.util.List;

public record ObjectData(
        int id,
        String name,
        List<PolygonPoint> polygon,
        List<Property> properties,
        String type,
        int width,
        int height,
        int x,
        int y
) {

}
