package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.ILifecycleService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldLoader implements ILifecycleService {

    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    PortalSource loadingSource;
    @Inject
    WorldRepository worldRepository;
    @Inject
    PreparationService preparationService;
    private Disposable regionSubscription;

    @Inject
    public WorldLoader() {
    }

    private void loadRegion() {
        IPortalController portalController = loadingSource.getPortalController();
        if (portalController != null) {
            portalController.loadWorld();
        }
    }

    public Completable loadWorld() {
        Region region = regionStorage.getRegion();
        if (region == null) {
            return Completable.never();
        }
        Area area = regionStorage.getArea();
        if (area == null || area.map() == null) {
            return Completable.never();
        }
        return preparationService.prepareWorld();
    }

    public Observable<Trainer> tryEnterRegion(Region region) {
        if (loadingSource.isTeleporting()) {
            return Observable.empty();
        }
        ensureRegionSubscription();
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
                    return regionService.getArea(region._id(), trainer.area())
                            .map((area) -> {
                                regionStorage.setRegion(region);
                                regionStorage.setArea(area);
                                trainerStorage.setTrainer(trainer);

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
        ensureRegionSubscription();
        if (loadingSource.isTeleporting()) {
            return Observable.empty();
        }
        Region region = regionStorage.getRegion();
        if (region == null) {
            return Observable.error(new IllegalStateException("No region in storage."));
        }
        loadingSource.setTeleporting(true);
        return regionService.getArea(trainer.region(), trainer.area())
                .map(area -> {
                    regionStorage.setArea(area);
                    trainerStorage.setTrainer(trainer);
                    loadRegion();
                    return trainer;
                }).doOnComplete(() -> loadingSource.setTeleporting(false));
    }

    private void ensureRegionSubscription() {
        if (regionSubscription == null) {
            regionSubscription = regionStorage.onEvents()
                    .subscribe(event -> worldRepository.reset(!event.changedArea()));
        }
    }

    @Override
    public void destroy() {
        if (regionSubscription != null) {
            regionSubscription.dispose();
        }
        worldRepository.reset(true);
    }

}
