package de.uniks.stpmon.k.models.map.layerdata;

import java.util.List;

import de.uniks.stpmon.k.models.map.Property;

public record ObjectData (
    int id,
    String name,
    List<Property> properties,
    String type,
    boolean visible,
    int rotation,
    int width,
    int height,
    int x,
    int y
) {
    
}
