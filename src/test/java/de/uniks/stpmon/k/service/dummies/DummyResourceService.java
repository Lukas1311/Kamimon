package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.service.IResourceService;
import io.reactivex.rxjava3.core.Observable;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@Singleton
public class DummyResourceService implements IResourceService {
    private BufferedImage tileSetImage;
    private BufferedImage characterImage;

    @Inject
    public DummyResourceService() {
    }

    @Override
    public Observable<BufferedImage> getCharacterImage(String name) {
        if (characterImage == null) {
            return Observable.create(emitter -> {
                try {
                    characterImage = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("map/char.png")));
                    emitter.onNext(characterImage);
                    emitter.onComplete();
                } catch (IOException e) {
                    emitter.onError(e);
                }
            });
        }
        return Observable.just(characterImage);
    }

    @Override
    public Observable<BufferedImage> getTilesetImage(String fileName) {
        if (tileSetImage == null) {
            return Observable.create(emitter -> {
                try {
                    characterImage = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("map/exteriors.png")));
                    emitter.onNext(characterImage);
                    emitter.onComplete();
                } catch (IOException e) {
                    emitter.onError(e);
                }
            });
        }
        return Observable.just(tileSetImage);
    }

    @Override
    public Observable<TilesetData> getTilesetData(String fileName) {
        return Observable.just(DummyConstants.TILESET_DATA);
    }
}
