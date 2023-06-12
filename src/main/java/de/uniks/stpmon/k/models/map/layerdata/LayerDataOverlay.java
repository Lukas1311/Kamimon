package de.uniks.stpmon.k.models.map.layerdata;

import java.util.List;

public record LayerDataOverlay(
    int id,
    String name,
    List<ChunkData> chunks,
    int x,
    int y,
    int width,
    int height,
    int startx,
    int starty,
    String type,
    boolean visible,
    List<Property> properties
) implements ITileLayerData {}
