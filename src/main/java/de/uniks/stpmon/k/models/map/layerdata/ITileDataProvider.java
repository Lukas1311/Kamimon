package de.uniks.stpmon.k.models.map.layerdata;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public interface ITileDataProvider {

    List<Integer> data();

    int width();

    int height();

    int startx();

    int starty();

}
