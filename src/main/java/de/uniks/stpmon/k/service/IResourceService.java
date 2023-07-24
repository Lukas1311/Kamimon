package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.map.TilesetData;
import io.reactivex.rxjava3.core.Observable;

import java.awt.image.BufferedImage;

public interface IResourceService {

    Observable<BufferedImage> getCharacterImage(String name);

    Observable<BufferedImage> getTilesetImage(String fileName);

    Observable<TilesetData> getTilesetData(String fileName);

    Observable<BufferedImage> getMonsterImage(String fileName);

    Observable<BufferedImage> getItemImage(String fileName);

}
