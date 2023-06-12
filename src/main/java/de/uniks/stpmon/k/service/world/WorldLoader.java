package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.CharacterSetCache;
import de.uniks.stpmon.k.world.PropMap;
import de.uniks.stpmon.k.world.WorldSet;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.List;

@Singleton
public class WorldLoader {

    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    PortalSource loadingSource;
    @Inject
    WorldStorage worldStorage;
    @Inject
    TextureSetService textureSetService;
    @Inject
    CacheManager cacheManager;

    @Inject
    public WorldLoader() {
    }

    private void loadRegion() {
        IPortalController portalController = loadingSource.getPortalController();
        if (portalController != null) {
            portalController.loadWorld();
        }
    }

    public Observable<WorldSet> getOrLoadWorld() {
        if (!worldStorage.isEmpty()) {
            return Observable
                    .just(worldStorage.getWorld());
        }
        return loadWorld().map((world) -> {
            worldStorage.setWorld(world);
            return world;
        });
    }

    public Observable<WorldSet> loadWorld() {
        Region region = regionStorage.getRegion();
        if (region == null) {
            return Observable.empty();
        }
        Area area = regionStorage.getArea();
        if (area == null || area.map() == null) {
            return Observable.empty();
        }
        // Init cache
        CharacterSetCache cache = cacheManager.characterSetCache();
        return cache.onInitialized().andThen(textureSetService.createMap(area).map((tileMap) -> {
            BufferedImage image = tileMap.renderMap();
            PropMap propMap = new PropMap(tileMap);
            List<TileProp> props = propMap.createProps();
            return new WorldSet(tileMap.getLayers().get(0), image, props);
        }));
    }

    public Observable<Trainer> tryEnterRegion(Region region) {
        if (loadingSource.isTeleporting()) {
            return Observable.empty();
        }
        Trainer currentTrainer = trainerStorage.getTrainer();
        if (currentTrainer != null) {
            if (currentTrainer.region().equals(region._id())) {
                return Observable.error(new IllegalStateException("Already in this region."));
            }
            return Observable.error(new IllegalStateException("Old trainer was not removed from storage."));
        }
        loadingSource.setTeleporting(true);
        return regionService.getMainTrainer(region._id())
                .flatMap((trainer) -> {
                    // Skip enter if trainer empty
                    if (trainer == NoneConstants.NONE_TRAINER) {
                        return Observable.just(trainer);
                    }
                    trainerStorage.setTrainer(trainer);
                    return regionService.getArea(region._id(), trainer.area())
                            .map((area) -> {
                                regionStorage.setRegion(region);
                                regionStorage.setArea(area);
                                worldStorage.setWorld(null);

                                loadRegion();
                                return trainer;
                            });
                })
                .doOnComplete(() -> loadingSource.setTeleporting(false));
    }

    public Observable<Trainer> tryEnterArea(Trainer trainer) {
        if (trainer == null || trainer == NoneConstants.NONE_TRAINER) {
            return Observable.error(new IllegalArgumentException("Trainer is null or none."));
        }
        if (loadingSource.isTeleporting()) {
            return Observable.empty();
        }
        loadingSource.setTeleporting(true);
        Region region = regionStorage.getRegion();
        if (region == null) {
            return Observable.error(new IllegalStateException("No region in storage."));
        }
        return regionService.getArea(trainer.region(), trainer.area())
                .map(area -> {
                    regionStorage.setArea(area);
                    worldStorage.setWorld(null);
                    trainerStorage.setTrainer(trainer);
                    loadRegion();
                    return trainer;
                }).doOnComplete(() -> loadingSource.setTeleporting(false));
    }
}
