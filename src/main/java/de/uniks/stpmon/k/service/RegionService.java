package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RegionService {
    @Inject
    RegionApiService regionApiService;

    @Inject
    RegionStorage regionStorage;

    @Inject
    UserStorage userStorage;

    @Inject
    public RegionService() {

    }

    //---------------- Region Trainers ----------------------------
    public Observable<Trainer> createTrainer(String regionId, String name, String image) {
        CreateTrainerDto dto = new CreateTrainerDto(name, image);
        return regionApiService.createTrainer(regionId, dto);
    }

    public Observable<List<Trainer>> getTrainers(String regionId, String areaId) {
        return regionApiService.getTrainers(regionId, areaId, userStorage.getUser()._id());
    }

    public Observable<Trainer> getTrainer(String trainerId) {
        return regionApiService.getTrainer(trainerId);
    }

    public Observable<Trainer> deleteTrainer(String trainerId) {
        return regionApiService.deleteTrainer(trainerId);
    }

    //------------------- Regions ---------------------------------
    public Observable<List<Region>> getRegions() {
        return regionApiService.getRegions();
    }

    public Observable<Region> getRegion(String id) {
        return regionApiService.getRegion(id);
    }

    public Observable<Area> enterRegion(Region region) {
        if (region.spawn() == null) {
            return Observable.error(new Exception("Region has no spawn."));
        }
        if (region.spawn().area() == null) {
            return Observable.error(new Exception("Spawn has no area."));
        }
        return getArea(region._id(), region.spawn().area()).map(area -> {
            regionStorage.setRegion(region);
            regionStorage.setArea(area);
            return area;
        });
    }

    //---------------- Region Areas ------------------------------
    public Observable<Area> getArea(String regionId, String areaId) {
        return regionApiService.getArea(regionId, areaId);
    }

    public Observable<List<Area>> getAreas(String regionId) {
        return regionApiService.getAreas(regionId);
    }

    //------------- Trainer Monsters -------------------------------
    public Observable<List<Monster>> getMonsters(String trainerId) {
        return regionApiService.getMonsters(trainerId);
    }

    public Observable<Monster> getMonster(String monsterId) {
        return regionApiService.getMonster(monsterId);
    }
}
