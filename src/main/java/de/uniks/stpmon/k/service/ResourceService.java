package de.uniks.stpmon.k.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.utils.ResponseUtils;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

@Singleton
public class ResourceService implements IResourceService {
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
        return ResponseUtils.readImage(presetService.getMonsterImage(fileName));
    }
}
