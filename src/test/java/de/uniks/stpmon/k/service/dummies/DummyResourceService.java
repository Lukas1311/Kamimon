package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.world.CharacterSet;
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
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    public Observable<BufferedImage> getCharacterImage(String name) {
        if (characterImage == null) {
            return Observable.create(emitter -> {
                try {
                    characterImage = ImageIO.read(Objects.requireNonNull(CharacterSet.class.getResourceAsStream("char.png")));
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
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    public Observable<BufferedImage> getTilesetImage(String fileName) {
        if (tileSetImage == null) {
            return Observable.create(emitter -> {
                try {
                    tileSetImage = ImageIO.read(Objects.requireNonNull(CharacterSet.class.getResourceAsStream("exteriors.png")));
                    emitter.onNext(tileSetImage);
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

    @Override
    public Observable<BufferedImage> getMonsterImage(String fileName) {
        return Observable.just(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
    }

    @Override
    public Observable<BufferedImage> getItemImage(String fileName) {
        return Observable.just(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
    }

}
