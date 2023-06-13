package de.uniks.stpmon.k.di;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Spawn;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Module
public class RegionTestModule {

    @Provides
    @Singleton
    static RegionApiService regionApiService() {
        return new RegionApiService() {
            final String USER_ID = "0";
            int trainerIdCount = 0;
            int monsterIdCount = 0;
            final List<Region> regions = new ArrayList<>();
            //String is regionId
            final Map<String, List<Area>> areasHashMap = new LinkedHashMap<>();
            final Map<String, List<Trainer>> trainersHashMap = new HashMap<>();
            final List<Monster> monsters = new ArrayList<>();

            /**
             * Adds 2 DummyRegions to the regions list with ids {"id0", "id1"} and
             * names {"TestRegion0", "TestRegion1"}
             */
            private void initDummyRegions() {
                Region region0 = new Region("id0", "TestRegion0", new Spawn("id0_0", 0, 0), null);
                Region region1 = new Region("id1", "TestRegion1", new Spawn("id0_0", 0, 0), null);

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
                        Area area = new Area(id, region._id(), name, DummyConstants.AREA_MAP_DATA);
                        areas.add(area);
                    }
                    areasHashMap.put(region._id(), areas);
                }
                initDummyTrainers();
            }

            /**
             * The same monster is added to all trainers
             */
            private void initDummyMonsters() {
                SortedMap<String, Integer> abilities = new TreeMap<>();
                abilities.put("1", 15);
                abilities.put("2", 10);
                abilities.put("7", 5);
                abilities.put("10", 0);

                MonsterAttributes attributes = new MonsterAttributes(0, 0, 0, 0);
                MonsterAttributes currentAttributes = new MonsterAttributes(0, 0, 0, 0);
                for (List<Trainer> trainerList : trainersHashMap.values()) {
                    for (Trainer trainer : trainerList) {
                        Monster monster = new Monster(
                                String.valueOf(monsterIdCount),
                                trainer._id(),
                                0,
                                0,
                                0,
                                abilities,
                                attributes,
                                currentAttributes);
                        monsterIdCount++;
                        monsters.add(monster);
                    }
                }

            }

            /**
             * Adds 1 Trainer to each area
             */
            private void initDummyTrainers() {
                for (Region region : regions) {
                    for (Area area : areasHashMap.get(region._id())) {
                        String name = region.name() + area.name() + "DummyTrainer";
                        String trainerImage = "trainer_" + trainerIdCount + ".png";
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
                        trainersHashMap.put(area._id(), trainers);
                        trainerIdCount++;
                    }
                }
                initDummyMonsters();
            }

            private Trainer getTrainerById(String trainerId) {
                if (regions.isEmpty()) {
                    initDummyRegions();
                }
                for (String areaId : trainersHashMap.keySet()) {
                    List<Trainer> trainers = trainersHashMap.get(areaId);
                    Optional<Trainer> trainerOp = trainers.stream().filter(t -> t._id().equals(trainerId)).findFirst();
                    if (trainerOp.isPresent()) {
                        return trainerOp.get();
                    }
                }
                return null;
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
                        area._id(),
                        0,
                        0,
                        0,
                        npcInfo
                );
                trainerIdCount++;
                List<Trainer> trainers = trainersHashMap.get(area._id());
                trainers.add(trainer);

                return Observable.just(trainer);
            }

            @Override
            public Observable<List<Trainer>> getTrainers(String regionId, String areaId) {
                List<Trainer> trainerList = trainersHashMap.get(areaId);
                if (trainerList != null) {
                    return Observable.just(trainerList);
                }
                return Observable.empty();
            }

            @Override
            public Observable<List<Trainer>> getTrainers(String regionId) {
                if (regions.isEmpty()) {
                    initDummyRegions();
                }
                return Observable.just(trainersHashMap.values()
                        .stream().flatMap(List::stream)
                        .collect(Collectors.toList()));
            }

            @Override
            public Observable<List<Trainer>> getMainTrainers(String regionId, String userId) {
                return getTrainer("", "4")
                        .switchIfEmpty(Observable.just(NoneConstants.NONE_TRAINER))
                        .map(t -> List.of(Objects.requireNonNullElse(t, NoneConstants.NONE_TRAINER)));
            }

            @Override
            public Observable<Trainer> getTrainer(String regionId, String trainerId) {
                Trainer trainer = getTrainerById(trainerId);
                return Observable.just(Objects.requireNonNullElse(trainer, NoneConstants.NONE_TRAINER));
            }

            @Override
            public Observable<Trainer> updateTrainer(String regionId, String trainerId, UpdateTrainerDto trainerDto) {
                Trainer trainer = getTrainerById(trainerId);
                if (trainer != null) {
                    return Observable.just(trainer);
                }
                return Observable.empty();
            }

            @Override
            public Observable<Trainer> deleteTrainer(String regionID, String trainerId) {
                Trainer trainer = getTrainerById(trainerId);
                if (trainer != null) {
                    List<Trainer> trainerList = trainersHashMap.get(trainer.area());
                    trainerList.remove(trainer);
                    return Observable.just(trainer);
                }
                return Observable.error(new Throwable("404 Not found"));
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
                if (regions.isEmpty()) {
                    initDummyRegions();
                }
                List<Area> areaList = areasHashMap.get(region);
                if (areaList != null) {
                    return Observable.just(areaList);
                }
                return Observable.empty();
            }

            @Override
            public Observable<Area> getArea(String region, String id) {
                if (regions.isEmpty()) {
                    initDummyRegions();
                }
                List<Area> areaList = areasHashMap.get(region);
                if (areaList == null || areaList.isEmpty()) {
                    return Observable.error(new Throwable("404 Not found"));
                }
                Optional<Area> areaOptional = areaList.stream().filter(a -> a._id().equals(id)).findFirst();
                return areaOptional.map(Observable::just).orElseGet(Observable::empty);
            }

            @Override
            public Observable<List<Monster>> getMonsters(String regionId, String trainerId) {
                return Observable.just(monsters.stream().filter(m -> m.trainer().equals(trainerId)).toList());

            }

            @Override
            public Observable<Monster> getMonster(String regionId, String monsterId) {
                Optional<Monster> monsterOptional = monsters
                        .stream().filter(m -> m._id().equals(monsterId)).findFirst();
                return monsterOptional.map(m -> Observable.just(monsterOptional.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }
        };
    }

}
