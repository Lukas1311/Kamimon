package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import de.uniks.stpmon.k.world.PropInspector;
import de.uniks.stpmon.k.world.PropMap;
import de.uniks.stpmon.k.world.TileMap;
import io.reactivex.rxjava3.core.Completable;
import retrofit2.HttpException;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
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
    RegionService regionService;

    @Inject
    public PreparationService() {
    }

    public Completable prepareLobby() {
        // Wait for region list to be loaded
        return tryCompleteWithRateLimit(() -> regionService.getRegions().ignoreElements())
                // Wait for region images to be loaded
                .andThen(tryCompleteWithRateLimitAndWait(() -> regionService.loadAllRegionImages()));
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
     * This will wait for the previous completable to complete before executing the next one.
     *
     * @param supplier Supplier which provides the completable to complete
     * @return Completable which completes after the supplied completable completed.
     */
    public Completable tryCompleteWithRateLimitAndWait(Supplier<Completable> supplier) {
        return Completable.create((e) -> e.setDisposable(
                tryCompleteWithRateLimit(supplier).subscribe(e::onComplete, e::onError)));

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
        return regionService.getRegionImage(region._id()).flatMapCompletable(
                (image) -> {
                    worldRepository.regionMap().setValue(image.defaultImage());
                    return Completable.complete();
                });
    }

    private PropMap createPropMap(TileMap tileMap, Area area) {
        List<DecorationLayer> decorationLayers = tileMap.renderDecorations();
        if (decorationLayers.isEmpty()) {
            return new PropMap(List.of(), new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        }
        TileMapData data = tileMap.getData();
        PropInspector inspector = new PropInspector(tileMap.getWidth(), tileMap.getHeight(), decorationLayers.size());
        inspector.setup(area);
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
                    PropMap propMap = createPropMap(tileMap, area);
                    List<TileProp> props = propMap.props();
                    BufferedImage floorImage = mergeFloor(allLayersImage, tileMap, propMap);
                    worldRepository.floorImage().setValue(floorImage);
                    worldRepository.minimapImage().setValue(allLayersImage);
                    worldRepository.props().setValue(props);
                    BufferedImage shadows = createShadows(props, floorImage);
                    worldRepository.shadowImage().setValue(shadows);
                    try {
                        ImageIO.write(shadows, "png", new File("shadow.png"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Completable.complete();
                });
    }

    public BufferedImage createShadows() {
        return createShadows(worldRepository.props().asNullable(), worldRepository.floorImage().asNullable());
    }

    public BufferedImage createShadows(List<TileProp> props, BufferedImage originalImage) {
        BufferedImage shadowImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = shadowImage.createGraphics();
        for (TileProp prop : props) {
            AffineTransform transform = new AffineTransform();
            transform.translate(prop.x() * 16 - prop.width() * 8, prop.y() * 16 + prop.height() * 16);
            transform.shear(-1f, 0);
            //transform.scale(1, 1f);
            transform.translate(prop.width() * 8, -prop.height() * 16);
            g.setTransform(transform);
            g.drawImage(prop.image(), 0, 0, null);
        }
        g.setTransform(new AffineTransform());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));
        g.setColor(new Color(0, 0, 0, 0.25f));
        g.fillRect(0, 0, shadowImage.getWidth(), shadowImage.getHeight());
        g.dispose();

        return shadowImage;
    }

}
