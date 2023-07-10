package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.RegionImage;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.storage.cache.RegionCache;
import de.uniks.stpmon.k.service.storage.cache.RegionImageCache;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RegionService {

    @Inject
    RegionApiService regionApiService;
    @Inject
    UserStorage userStorage;
    @Inject
    Provider<RegionCache> regionCacheProvider;
    @Inject
    Provider<RegionImageCache> regionMapCacheProvider;
    private RegionCache regionCache;
    private RegionImageCache regionImageCache;

    @Inject
    public RegionService() {

    }

    //---------------- Region Trainers ----------------------------
    public Observable<Trainer> createTrainer(String regionId, String name, String image) {
        CreateTrainerDto dto = new CreateTrainerDto(name, image);
        return regionApiService.createTrainer(regionId, dto);
    }

    public Observable<List<Trainer>> getTrainers(String regionId, String areaId) {
        return regionApiService.getTrainers(regionId, areaId);
    }

    public Observable<List<Trainer>> getTrainers(String regionId) {
        return regionApiService.getTrainers(regionId);
    }

    public Observable<Trainer> getMainTrainer(String regionId) {
        return regionApiService.getMainTrainers(regionId, userStorage.getUser()._id())
                .flatMap((trainers) -> {
                    if (trainers.isEmpty()) {
                        return Observable.just(NoneConstants.NONE_TRAINER);
                    }
                    return Observable.just(trainers.get(0));
                });
    }

    public Observable<Trainer> getTrainer(String regionID, String trainerId) {
        return regionApiService.getTrainer(regionID, trainerId);
    }

    public Observable<Trainer> deleteTrainer(String regionId, String trainerId) {
        return regionApiService.deleteTrainer(regionId, trainerId);
    }

    //------------------- Regions ---------------------------------
    public Observable<List<Region>> getRegions() {
        if (regionCache == null) {
            regionCache = regionCacheProvider.get();
            regionCache.init();
        }
        return regionCache.onInitialized()
                .andThen(regionCache.getValues().take(1));
    }

    public Observable<Region> getRegion(String id) {
        if (regionCache == null) {
            regionCache = regionCacheProvider.get();
            regionCache.init();
        }
        return regionCache.getValue(id).map(Observable::just)
                .orElseGet(Observable::empty);
    }

    public Observable<RegionImage> getRegionImage(String regionId) {
        if (regionImageCache == null) {
            regionImageCache = regionMapCacheProvider.get();
            regionImageCache.init();
        }
        return regionImageCache.onInitialized()
                .andThen(regionImageCache.getLazyValue(regionId).flatMap(op ->
                        op.map(Observable::just).orElse(Observable.empty())));
    }

    public Completable loadAllRegionImages() {
        if (regionImageCache == null) {
            regionImageCache = regionMapCacheProvider.get();
            regionImageCache.init();
        }
        return regionImageCache.getLazyValues(regionCache.getIds())
                .ignoreElements();
    }

    //---------------- Region Areas ------------------------------
    public Observable<Area> getArea(String regionId, String areaId) {
        return regionApiService.getArea(regionId, areaId);
    }

    public Observable<List<Area>> getAreas(String regionId) {
        return regionApiService.getAreas(regionId);
    }

    //------------- Trainer Monsters -------------------------------
    public Observable<List<Monster>> getMonsters(String regionId, String trainerId) {
        return regionApiService.getMonsters(regionId, trainerId);
    }

    public Observable<Monster> getMonster(String regionId, String trainer, String monsterId) {
        return regionApiService.getMonster(regionId, trainer, monsterId);
    }

}
