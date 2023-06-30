package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import de.uniks.stpmon.k.world.PropInspector;
import de.uniks.stpmon.k.world.PropMap;
import de.uniks.stpmon.k.world.TileMap;
import io.reactivex.rxjava3.core.Completable;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Singleton
public class PreparationService {

    public static final int RATE_LIMIT_TIMEOUT = 1;

    @Inject
    WorldRepository worldRepository;
    @Inject
    TextureSetService textureSetService;
    @Inject
    CacheManager cacheManager;
    @Inject
    RegionStorage regionStorage;
    @Inject
    PresetService presetService;

    @Inject
    public PreparationService() {
    }

    public Completable prepareLobby() {
        // Currently we don't need to load anything for the lobby.
        return Completable.complete();
    }

    /**
     * Loads all the data needed for the world to be displayed.
     * This includes the characters, the region map and the world.
     *
     * @return Completable which completes after all data is loaded.
     */
    public Completable prepareWorld() {
        return tryCompleteWithRateLimit(this::loadAreaCharacters)
                .andThen(tryCompleteWithRateLimit(this::loadRegionMap))
                .andThen(tryCompleteWithRateLimit(this::loadAreaAndProps));
    }

    /**
     * Tries to complete the supplied completable. If it fails, it will retry after a minute.
     * If it fails again, it will direct the error down the stream.
     *
     * @param supplier Supplier which provides the completable to complete
     * @return Completable which completes after the supplied completable completed.
     */
    public Completable tryCompleteWithRateLimit(Supplier<Completable> supplier) {
        Completable completable = supplier.get();
        return completable.onErrorResumeNext(
                (error) -> {
                    if (error instanceof HttpException httpException) {
                        if (httpException.code() == 429) {
                            return completable.delaySubscription(RATE_LIMIT_TIMEOUT, TimeUnit.MINUTES);
                        }
                    }
                    return Completable.error(error);
                });
    }

    public Completable loadAreaCharacters() {
        TrainerAreaCache areaCache = cacheManager.trainerAreaCache();
        return areaCache.onInitialized()
                // just take the first values
                .andThen(areaCache.getValues().take(1))
                .flatMapCompletable((trainers) ->
                        Completable.fromObservable(
                                cacheManager.characterSetCache().getLazyValues(
                                        trainers.stream().map(Trainer::image)
                                                .collect(Collectors.toSet()))
                        ));
    }

    public Completable loadRegionMap() {
        Region region = regionStorage.getRegion();
        // don't load the region map if it is already loaded
        if (!worldRepository.regionMap().isEmpty()) {
            return Completable.complete();
        }
        return textureSetService.createMap(region).flatMapCompletable(
                (tileMap) -> {
                    BufferedImage allLayersImage = tileMap.renderMap();
                    worldRepository.regionMap().setValue(allLayersImage);
                    return Completable.complete();
                });
    }

    private PropMap createPropMap(TileMap tileMap) {
        List<DecorationLayer> decorationLayers = tileMap.renderDecorations();
        if (decorationLayers.isEmpty()) {
            return new PropMap(List.of(), new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        }
        TileMapData data = tileMap.getData();
        PropInspector inspector = new PropInspector(data.width(), data.height(), decorationLayers.size());
        return inspector.work(decorationLayers, data);
    }

    private BufferedImage mergeFloor(BufferedImage image, TileMap tileMap, PropMap propMap) {
        BufferedImage realFloorImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = realFloorImage.createGraphics();
        g.drawImage(tileMap.renderFloor(), 0, 0, null);
        g.drawImage(propMap.decorations(), 0, 0, null);
        g.dispose();
        return realFloorImage;
    }

    /**
     * Loads the floor texture and texture set from
     */
    public Completable loadAreaAndProps() {
        Area area = regionStorage.getArea();
        return textureSetService.createMap(area).flatMapCompletable(
                (tileMap) -> {
                    BufferedImage allLayersImage = tileMap.renderMap();
                    PropMap propMap = createPropMap(tileMap);
                    List<TileProp> props = propMap.props();
                    BufferedImage floorImage = mergeFloor(allLayersImage, tileMap, propMap);
                    worldRepository.floorImage().setValue(floorImage);
                    worldRepository.minimapImage().setValue(allLayersImage);
                    worldRepository.props().setValue(props);
                    return Completable.complete();
                });
    }
}
