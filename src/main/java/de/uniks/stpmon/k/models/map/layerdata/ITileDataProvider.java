package de.uniks.stpmon.k.models.map.layerdata;

import java.util.List;

public interface ITileDataProvider {

    List<Integer> data();

    int width();

    int height();

}
