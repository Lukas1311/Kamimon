package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.service.IResourceService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

@Singleton
public class DummyResourceService implements IResourceService {

    @Inject
    public DummyResourceService() {
    }

    @Override
    public Observable<BufferedImage> getCharacterImage(String name) {
        return Observable.empty();
    }

    @Override
    public Observable<BufferedImage> getTilesetImage(String fileName) {
        return Observable.empty();
    }

    @Override
    public Observable<TilesetData> getTilesetData(String fileName) {
        return Observable.empty();
    }
}
