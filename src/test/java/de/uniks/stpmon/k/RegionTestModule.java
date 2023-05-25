package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.*;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;
import java.util.*;

@Module
public class RegionTestModule {

    @Provides
    @Singleton
    static RegionApiService regionApiService(){
        return new RegionApiService() {

            final String USER_ID = "0";
            int trainerIdCount = 0;
            final List<Region> regions = new ArrayList<>();
            //String is regionId
            final Map<String, List<Area>> areasHashMap = new LinkedHashMap<>();
            final Map<Area, List<Trainer>> trainersHashMap = new HashMap<>();

            /**
             * Adds 2 DummyRegions to the regions list with ids {"id0", "id1"} and
             * names {"TestRegion0", "TestRegion1"}
             */
            private void initDummyRegions() {
                Region region0 = new Region("id0", "TestRegion0", null, null);
                Region region1 = new Region("id1", "TestRegion1", null, null);

                regions.add(region0);
                regions.add(region1);

                initDummyAreas();

            }

            /**
             * Adds 2 DummyAreas to each region
             * id = [regionId]_[i]> -> e.g. id0_0
             */
            private void initDummyAreas() {
                for (Region region : regions) {
                    List<Area> areas = new ArrayList<>();
                    for (int i = 0; i < 2; i++) {
                        String id = region._id() + "_" + i;
                        String name = region.name() + "DummyArea" + i;
                        //TODO: Add mock for map
                        Area area = new Area(id, region._id(), name, null);
                        areas.add(area);
                    }
                    areasHashMap.put(region._id(), areas);
                }
                initDummyTrainers();
            }

            /**
             * Adds 1 Trainer to each area
             */
            private void initDummyTrainers() {
                for (Region region : regions) {
                    for (Area area : areasHashMap.get(region._id())) {
                        String name = region.name() + area.name() + "DummyTrainer";
                        String trainerImage = "TrainerImage" + trainerIdCount;
                        Trainer trainer = new Trainer(
                                Integer.toString(trainerIdCount),
                                region._id(),
                                USER_ID,
                                name,
                                trainerImage,
                                0,
                                area._id(),
                                0,
                                0,
                                0,
                                null);
                        ArrayList<Trainer> trainers = new ArrayList<>();
                        trainers.add(trainer);
                        trainersHashMap.put(area, trainers);
                        trainerIdCount++;
                    }
                }

            }

            /**
             * trainers are added always to first area the region
             */
            @Override
            public Observable<Trainer> createTrainer(String regionId, CreateTrainerDto trainerDto) {
                Area area = areasHashMap.get(regionId).get(0);
                NPCInfo npcInfo = new NPCInfo(true);
                Trainer trainer = new Trainer(
                        String.valueOf(trainerIdCount),
                        regionId,
                        USER_ID,
                        trainerDto.name(),
                        trainerDto.image(),
                        0,
                        String.valueOf(area),
                        0,
                        0,
                        0,
                        npcInfo
                );
                trainerIdCount++;
                List<Trainer> trainers = trainersHashMap.get(area);
                trainers.add(trainer);

                return Observable.just(trainer);
            }

            @Override
            public Observable<List<Trainer>> getTrainers(String regionId, String areaId, String userId) {
                return null;
            }

            @Override
            public Observable<Trainer> getTrainer(String trainerId) {
                return null;
            }

            @Override
            public Observable<Trainer> deleteTrainer(String trainerId) {
                return null;
            }

            /**
             * Returns all regions (if list of regions is empty, it gets initialized
             */
            @Override
            public Observable<List<Region>> getRegions() {
                if (regions.isEmpty()) {
                    initDummyRegions();
                }
                return Observable.just(regions);
            }

            /**
             * Returns all region with id (if list of regions is empty, it gets initialized
             */
            @Override
            public Observable<Region> getRegion(String id) {
                if (regions.isEmpty()) {
                    initDummyRegions();
                }

                Optional<Region> region = regions.stream().filter(r -> r._id().equals(id)).findFirst();
                return region.map(r -> Observable.just(region.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }

            @Override
            public Observable<List<Area>> getAreas(String region) {
                return null;
            }

            @Override
            public Observable<Area> getArea(String region, String id) {
                return null;
            }

            @Override
            public Observable<List<Monster>> getMonsters(String trainerId) {
                return null;
            }

            @Override
            public Observable<Monster> getMonster(String monsterId) {
                return null;
            }
        };
    }

}
