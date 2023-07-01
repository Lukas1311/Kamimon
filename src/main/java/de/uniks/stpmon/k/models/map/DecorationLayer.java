package de.uniks.stpmon.k.models.map;

import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.awt.image.BufferedImage;

public record DecorationLayer(TileLayerData layerData, int layerIndex, BufferedImage image) {

}
