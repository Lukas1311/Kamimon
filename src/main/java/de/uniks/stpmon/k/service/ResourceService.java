package de.uniks.stpmon.k.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.utils.ResponseUtils;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ResourceService implements IResourceService {
    private final Map<String, BufferedImage> monsterImages = new HashMap<>();
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
        if (!monsterImages.containsKey(fileName)) {
            return ResponseUtils.readImage(presetService.getMonsterImage(fileName)).map(image -> {
                monsterImages.put(fileName, image);
                return image;
            });
        }
        return Observable.just(monsterImages.get(fileName));
    }

}
