package de.uniks.stpmon.k.models.map;

import java.util.List;

/**
 * @param colors    Array of Wang colors (since 1.5)
 * @param name      Name of the Wang set
 * @param tile      Local ID of tile representing the Wang set
 * @param type      corner, edge or mixed (since 1.5)
 * @param wangtiles Array of Wang tiles
 */
@SuppressWarnings("SpellCheckingInspection")
public record WangSet(List<WangColor> colors,
                      String name,
                      int tile,
                      String type,
                      List<WangtTile> wangtiles) {
}
