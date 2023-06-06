package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.TileMapService;
import de.uniks.stpmon.k.service.world.World;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;

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
    TileMapService tileMapService;
    Timer timer;


    @Inject
    public WorldLoader() {
    }

    private void loadRegion() {
        IPortalController portalController = loadingSource.getPortalController();
        if (portalController != null) {
            portalController.loadWorld();
        }
    }

    public Observable<World> getOrLoadWorld() {
        if (!worldStorage.isEmpty()) {
            return Observable
                    .just(worldStorage.getWorld());
        }
        return tileMapService.loadTilemap().map((world) -> {
            worldStorage.setWorld(world);
            return world;
        });
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
                    trainerStorage.setTrainer(trainer);
                    return regionService.getArea(region._id(), trainer.area())
                            .map((area) -> {
                                regionStorage.setRegion(region);
                                regionStorage.setArea(area);
                                worldStorage.setWorld(null);

                                loadRegion();
                                return trainer;
                            });
                }).doOnComplete(() -> loadingSource.setTeleporting(false));
    }

    public Observable<Trainer> tryEnterArea() {
        if (loadingSource.isTeleporting()) {
            return Observable.empty();
        }
        loadingSource.setTeleporting(true);
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return Observable.error(new IllegalStateException("No trainer in storage."));
        }
        Region region = regionStorage.getRegion();
        if (region == null) {
            return Observable.error(new IllegalStateException("No region in storage."));
        }
        return regionService.getArea(trainer.region(), trainer.area())
                .map(area -> {
                    regionStorage.setArea(area);
                    worldStorage.setWorld(null);
                    loadRegion();
                    return trainer;
                }).doOnComplete(() -> loadingSource.setTeleporting(false));
    }
}
