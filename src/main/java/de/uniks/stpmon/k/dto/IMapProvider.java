package de.uniks.stpmon.k.dto;

import de.uniks.stpmon.k.models.map.TileMapData;

public interface IMapProvider {

    TileMapData map();

    String _id();

}
