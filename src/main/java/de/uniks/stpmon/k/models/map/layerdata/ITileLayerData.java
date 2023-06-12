package de.uniks.stpmon.k.models.map.layerdata;

public interface ITileLayerData {
    int id();
    String name();
    String type();
    boolean visible();
    int x();
    int y();
}
