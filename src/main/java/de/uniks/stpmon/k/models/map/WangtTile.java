package de.uniks.stpmon.k.models.map;

import java.util.List;

/**
 * @param tileid Local ID of tile
 * @param wangid Array of Wang color indexes (uchar[8])
 */
@SuppressWarnings("SpellCheckingInspection")
public record WangtTile(int tileid,
                        List<Integer> wangid) {
}
