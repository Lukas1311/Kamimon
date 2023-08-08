package de.uniks.stpmon.k.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.utils.ResponseUtils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.*;

@Singleton
public class ResourceService implements IResourceService {
    private final Map<String, BufferedImage> monsterImages = new HashMap<>();
    private final Set<String> awaitedMonsters = Collections.synchronizedSet(new LinkedHashSet<>());
    private final PublishSubject<String> monsterLoaded = PublishSubject.create();
    private final Map<String, BufferedImage> itemImages = new HashMap<>();
    @Inject
    protected PresetService presetService;
    @Inject
    protected ObjectMapper mapper;

    @Inject
    public ResourceService() {

    }

    public Observable<BufferedImage> getCharacterImage(String name) {
        return ResponseUtils.readImage(presetService.getCharacterFile(name));
    }

    public Observable<BufferedImage> getTilesetImage(String fileName) {
        return ResponseUtils.readImage(presetService.getFile(fileName));
    }

    public Observable<TilesetData> getTilesetData(String fileName) {
        return ResponseUtils.readJson(presetService.getFile(fileName), mapper, TilesetData.class);
    }

    public Observable<BufferedImage> getMonsterImage(String fileName) {
        if (monsterImages.containsKey(fileName)) {
            return Observable.just(monsterImages.get(fileName));
        }
        if (!awaitedMonsters.add(fileName)) {
            return monsterLoaded.filter(fileName::equals)
                    .map((g) -> monsterImages.get(fileName));
        }
        return ResponseUtils.readImage(presetService.getMonsterImage(fileName)).map(image -> {
            monsterImages.put(fileName, image);
            monsterLoaded.onNext(fileName);
            return image;
        });
    }

    public Observable<BufferedImage> getItemImage(String itemId) {
        if (!itemImages.containsKey(itemId)) {
            return ResponseUtils.readImage(presetService.getItemImage(itemId)).map(image -> {
                itemImages.put(itemId, image);
                return image;
            });
        }
        return Observable.just(itemImages.get(itemId));
    }

}
