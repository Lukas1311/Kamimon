package de.uniks.stpmon.k.utils;

import java.util.List;

public record World(
        TileMap map,
        List<TileProp> props) {
}
