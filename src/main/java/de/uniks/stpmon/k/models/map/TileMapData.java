package de.uniks.stpmon.k.models.map;

import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.util.Comparator;
import java.util.List;

/**
 * @param height     Number of tile rows
 * @param infinite   Whether the map has infinite dimensions
 * @param layers     Array of Layers
 * @param properties Array of Properties
 * @param tileheight Map grid height
 * @param tilesets   Array of Tilesets
 * @param tilewidth  Map grid width
 * @param type       map
 * @param width      Number of tile columns
 */
@SuppressWarnings("SpellCheckingInspection")
public record TileMapData(
        int width,
        int height,
        boolean infinite,
        List<TileLayerData> layers,
        List<Property> properties,
        int tilewidth,
        int tileheight,
        List<TilesetSource> tilesets,
        String type
) {

    public TilesetSource getTileset(int id) {
        return tilesets.stream()
                .sorted(Comparator.comparingInt(TilesetSource::firstgid).reversed())
                .filter(tileset -> (id - tileset.firstgid()) >= 0)
                .findFirst()
                .orElse(null);
    }

    public boolean isIndoor() {
        return properties() != null &&
                properties().stream().anyMatch(property ->
                        property.name().equals("Terrain") && property.value().equals("Building"));
    }
}
