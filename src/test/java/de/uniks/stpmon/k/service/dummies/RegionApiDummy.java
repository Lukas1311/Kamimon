package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Singleton
public class RegionApiDummy implements RegionApiService {
    final String USER_ID = "0";
    int trainerIdCount = 1;
    int monsterIdCount = 0;
    final List<Region> regions = new ArrayList<>();
    //String is regionId
    final Map<String, List<Area>> areasHashMap = new LinkedHashMap<>();
    final Map<String, List<Trainer>> trainersHashMap = new LinkedHashMap<>();

    final List<Monster> monsters = new ArrayList<>();

    @Inject
    public RegionApiDummy() {
    }

    /**
     * Adds 2 DummyRegions to the regions list with ids {"id0", "id1"} and
     * names {"TestRegion0", "TestRegion1"}
     */
    private void initDummyRegions() {
        Region region0 = new Region("id0", "TestRegion0", new Spawn("id0_0", 0, 0), DummyConstants.EMPTY_MAP_DATA);
        Region region1 = new Region("id1", "TestRegion1", new Spawn("id0_0", 0, 0), DummyConstants.EMPTY_MAP_DATA);

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

        monsters.add(MonsterBuilder.builder()
                .setId(monsterIdCount++)
                .setAbilities(abilities)
                .setAttributes(new MonsterAttributes(20, 0, 0, 0))
                .setCurrentAttributes(new MonsterAttributes(10, 0, 0, 0))
                .create());

        monsters.add(MonsterBuilder.builder()
                .setId(monsterIdCount++)
                .setAbilities(abilities)
                .setAttributes(new MonsterAttributes(12, 0, 0, 0))
                .setCurrentAttributes(new MonsterAttributes(5, 0, 0, 0))
                .create());

        for (List<Trainer> trainerList : trainersHashMap.values()) {
            for (Trainer trainer : trainerList) {
                monsters.add(MonsterBuilder.builder()
                        .setId(monsterIdCount++)
                        .setTrainer(trainer._id())
                        .setAbilities(abilities)
                        .setAttributes(attributes)
                        .setCurrentAttributes(currentAttributes)
                        .create());
            }
        }
    }

    /**
     * Adds 1 Trainer to each area
     */
    private void initDummyTrainers() {
        int monsterIdCount = 2;
        for (Region region : regions) {
            for (Area area : areasHashMap.get(region._id())) {
                String name = region.name() + area.name() + "DummyTrainer";
                String trainerImage = "trainer_" + trainerIdCount + ".png";
                Trainer trainer = TrainerBuilder.builder()
                        .setId(trainerIdCount)
                        .setRegion(region)
                        .setArea(area)
                        .setName(name)
                        .setImage(trainerImage)
                        .setUser(USER_ID)
                        .addTeam(Integer.toString(monsterIdCount++))
                        .create();
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
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        Area area = areasHashMap.get(regionId).get(0);

        Trainer trainer = TrainerBuilder.builder()
                .setId(0)
                .setRegion(regionId)
                .setArea(area)
                .setUser(USER_ID)
                .applyCreate(trainerDto)
                .create();
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
        if (userId.equals("00")) {
            List<Trainer> t = trainersHashMap.get("id0_0");
            return Observable.just(t);
        }
        return getTrainer("", "0")
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
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        return Observable.just(monsters.stream().filter(m -> m.trainer().equals(trainerId)).toList());
    }

    @Override
    public Observable<Monster> getMonster(String regionId, String trainer, String monsterId) {
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        Optional<Monster> monsterOptional = monsters
                .stream().filter(m -> m._id().equals(monsterId)).findFirst();
        return monsterOptional.map(m -> Observable.just(monsterOptional.get())).orElseGet(()
                -> Observable.error(new Throwable("404 Not found")));
    }
}