package de.uniks.stpmon.k.utils;

import de.uniks.stpmon.k.models.map.TileMapData;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PropInspector {
    private final BitSet visited;
    private final short[][] grid;
    private final Map<Integer, HashSet<Integer>> groups;

    public PropInspector(TileMapData data) {
        visited = new BitSet(data.width() * data.height());
        grid = new short[data.width()][data.height()];
        groups = new HashMap<>();
    }
}
