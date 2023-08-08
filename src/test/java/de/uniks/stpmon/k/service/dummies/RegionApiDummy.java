package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@SuppressWarnings("unused")
@Singleton
public class RegionApiDummy implements RegionApiService {
    private static final String TRAINER_ID = "0";
    public static final String REGION_ID = "id0";
    int trainerIdCount = 1;
    int monsterIdCount = 0;
    final List<Region> regions = new ArrayList<>();
    final List<Area> areas = new ArrayList<>();
    final Map<String, Trainer> trainers = new LinkedHashMap<>();
    final Map<String, Monster> monstersById = new HashMap<>();
    final Map<String, List<Monster>> monstersByTrainer = new HashMap<>();
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    EventDummy eventDummy;

    @Inject
    public RegionApiDummy() {
    }

    /**
     * Adds 2 DummyRegions to the regions list with ids {"id0", "id1"} and
     * names {"TestRegion0", "TestRegion1"}
     */
    private void initDummyRegions() {
        Region region0 = new Region(REGION_ID, "TestRegion0", new Spawn("id0_0", 0, 0), DummyConstants.EMPTY_MAP_DATA);
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
        for (int i = 0; i < 2; i++) {
            String id = REGION_ID + "_" + i;
            String name = "DummyArea" + i;
            Area area = new Area(id, REGION_ID, name, DummyConstants.AREA_MAP_DATA);
            areas.add(area);
        }
        initDummyTrainers();
    }

    /**
     * Initilizes the monstes
     */
    private void initDummyMonsters() {
        SortedMap<String, Integer> abilities = new TreeMap<>();
        abilities.put("1", 15);
        abilities.put("2", 10);
        abilities.put("7", 5);
        abilities.put("10", 0);

        MonsterAttributes attributes = new MonsterAttributes(0f, 0f, 0f, 0f);
        MonsterAttributes currentAttributes = new MonsterAttributes(0f, 0f, 0f, 0f);

        monstersById.put(Integer.toString(monsterIdCount), MonsterBuilder.builder()
                .setId(monsterIdCount++) //id=0
                .setAbilities(abilities)
                .setAttributes(new MonsterAttributes(20f, 0f, 0f, 0f))
                .setCurrentAttributes(new MonsterAttributes(1f, 0f, 0f, 0f))
                .setLevel(1)
                .setExperience(5)
                .create());

        monstersById.put(Integer.toString(monsterIdCount), MonsterBuilder.builder()
                .setId(monsterIdCount++) //id=1
                .setAbilities(abilities)
                .setType(2)
                .setAttributes(new MonsterAttributes(12f, 0f, 0f, 0f))
                .setCurrentAttributes(new MonsterAttributes(2f, 0f, 0f, 0f))
                .setLevel(2)
                .create());

        monstersById.put(Integer.toString(monsterIdCount), MonsterBuilder.builder()
                .setId(monsterIdCount++) //id=2
                .setAbilities(abilities)
                .setType(0)
                .setAttributes(new MonsterAttributes(100f, 0f, 0f, 0f))
                .setCurrentAttributes(new MonsterAttributes(100f, 0f, 0f, 0f))
                .setLevel(2)
                .create());

        monstersById.put(Integer.toString(monsterIdCount), MonsterBuilder.builder()
                .setId(monsterIdCount++) //id=3
                .setAbilities(abilities)
                .setType(3)
                .setAttributes(new MonsterAttributes(12f, 0f, 0f, 0f))
                .setCurrentAttributes(new MonsterAttributes(2f, 0f, 0f, 0f))
                .setLevel(2)
                .create());

        monstersById.put(Integer.toString(monsterIdCount), MonsterBuilder.builder()
                .setId(monsterIdCount++) //id=4
                .setAbilities(abilities)
                .setType(2)
                .setAttributes(new MonsterAttributes(12f, 0f, 0f, 0f))
                .setCurrentAttributes(new MonsterAttributes(0f, 0f, 0f, 0f))
                .setLevel(2)
                .create());

        monstersById.put(Integer.toString(monsterIdCount), MonsterBuilder.builder()
                .setId(monsterIdCount++) //id=5
                .setAbilities(abilities)
                .setType(2)
                .setAttributes(new MonsterAttributes(12f, 0f, 0f, 0f))
                .setCurrentAttributes(new MonsterAttributes(12f, 0f, 0f, 0f))
                .setExperience(10)
                .setLevel(2)
                .create());
    }

    /**
     * Adds 1 Trainer to each area
     */
    private void initDummyTrainers() {
        int monsterIdCount = 2;
        int userIdCount = 1;
        for (Area area : areas) {
            String name = REGION_ID + area.name() + "DummyTrainer";
            String trainerImage = "trainer_" + trainerIdCount + ".png";
            Trainer trainer = TrainerBuilder.builder()
                    .setId(trainerIdCount)
                    .setRegion(REGION_ID)
                    .setArea(area)
                    .setName(name)
                    .setImage(trainerImage)
                    .setUser(Integer.toString(userIdCount++))
                    .addTeam(Integer.toString(monsterIdCount++))
                    .create();
            trainers.put(trainer._id(), trainer);
            trainerIdCount++;
        }
        initDummyMonsters();
    }

    private Trainer getTrainerById(String trainerId) {
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        return trainers.get(trainerId);
    }

    /**
     * trainers are added always to first area the region
     */
    @Override
    public Observable<Trainer> createTrainer(String regionId, CreateTrainerDto trainerDto) {
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        if (!regionId.equals(REGION_ID)) {
            return Observable.empty();
        }
        Area area = areas.get(0);

        Trainer trainer = TrainerBuilder.builder()
                .setId(0)
                .setRegion(regionId)
                .setArea(area)
                .setUser(TRAINER_ID)
                .applyCreate(trainerDto)
                .create();
        trainers.put(TRAINER_ID, trainer);

        return Observable.just(trainer);
    }

    public void addMonster(String trainerId, String monsterId, boolean team) {
        Trainer trainer = getTrainerById(trainerId);
        if (team) {
            Trainer updated = TrainerBuilder.builder(trainer)
                    .addTeam(monsterId)
                    .create();
            trainers.put(trainerId, updated);
            if (trainerId.equals(TRAINER_ID)) {
                trainerStorage.setTrainer(updated);
            }
            eventDummy.sendEvent(new Event<>("regions.%s.trainers.%s.updated".formatted(REGION_ID, trainerId), updated));
        }
        Monster monster = monstersById.get(monsterId);
        Monster updated = MonsterBuilder.builder(monster)
                .setTrainer(trainerId)
                .create();
        monstersById.put(monsterId, updated);
        monstersByTrainer.putIfAbsent(trainerId, new ArrayList<>());
        List<Monster> monsters = monstersByTrainer.get(trainerId);
        monsters.add(updated);
        eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.created"
                .formatted(trainerId, monsterId), updated));
        List<Monster> oldMonsters = monstersByTrainer.get(monster.trainer());
        if (oldMonsters != null) {
            oldMonsters.removeIf(m -> m._id().equals(monsterId));
            eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.removed"
                    .formatted(monster.trainer(), monsterId), monster));
        }
    }

    public void addEncounteredMonsters(String trainerId, Integer monsterId) {
        Trainer trainer = getTrainerById(trainerId);
        Trainer updated = TrainerBuilder.builder(trainer)
                .addEncountered(monsterId)
                .create();
        if (trainerId.equals(TRAINER_ID)) {
            trainerStorage.setTrainer(updated);
        }
        eventDummy.sendEvent(new Event<>("regions.%s.trainers.%s.updated".formatted(REGION_ID, trainerId), updated));
    }

    public void updateMonster(Monster updatedTarget) {
        monstersById.put(updatedTarget._id(), updatedTarget);
        eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.updated".formatted(
                updatedTarget.trainer(),
                updatedTarget._id()),
                updatedTarget));
        Monster old = monstersById.get(updatedTarget._id());
        checkUpdateTrainer(old, updatedTarget);
    }



    private void checkUpdateTrainer(Monster oldMonster, Monster newMonster) {
        String trainerId = oldMonster.trainer();
        String monsterId = oldMonster._id();
        List<Monster> oldMonsters = monstersByTrainer.get(oldMonster.trainer());
        if (oldMonsters != null) {
            oldMonsters.removeIf(m -> m._id().equals(monsterId));
            eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.removed"
                    .formatted(oldMonster.trainer(), monsterId), oldMonster));
        }

        monstersByTrainer.putIfAbsent(trainerId, new ArrayList<>());
        List<Monster> monsters = monstersByTrainer.get(trainerId);
        int index = monsters.indexOf(newMonster);
        if (index == -1) {
            monsters.add(newMonster);
        } else {
            monsters.set(index, newMonster);
        }
        eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.created"
                .formatted(trainerId, monsterId), newMonster));
    }

    public void updateTrainerCoins(String trainerId, Integer newCoins, boolean isDifference) {
        Trainer trainer = getTrainerById(trainerId);
        Trainer updated = TrainerBuilder.builder(trainer)
                .setCoins(isDifference ? trainer.coins() + newCoins : newCoins)
                .create();
        if (trainerId.equals(TRAINER_ID)) {
            trainerStorage.setTrainer(updated);
        }
        eventDummy.sendEvent(new Event<>("regions.%s.trainers.%s.updated".formatted(REGION_ID, trainerId), updated));
    }

    public void addTrainer(Trainer trainer) {
        trainers.put(trainer._id(), trainer);
        eventDummy.sendEvent(new Event<>("regions.%s.trainers.%s.created"
                .formatted(REGION_ID, trainer._id()), trainer));
    }

    @Override
    public Observable<List<Trainer>> getTrainers(String regionId, String areaId) {
        if (!regionId.equals(REGION_ID)) {
            return Observable.just(List.of());
        }
        return Observable.just(List.copyOf(trainers.values()));
    }

    @Override
    public Observable<List<Trainer>> getTrainers(String regionId) {
        if (!regionId.equals(REGION_ID)) {
            return Observable.empty();
        }
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        return Observable.just(List.copyOf(trainers.values()));
    }

    @Override
    public Observable<List<Trainer>> getMainTrainers(String regionId, String userId) {
        return getTrainer("", TRAINER_ID)
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
            trainers.remove(trainer._id());
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
        if (!region.equals(REGION_ID)) {
            return Observable.just(List.of());
        }
        return Observable.just(areas);
    }

    @Override
    public Observable<Area> getArea(String region, String id) {
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        if (areas.isEmpty()) {
            return Observable.error(new Throwable("404 Not found"));
        }
        Optional<Area> areaOptional = areas.stream().filter(a -> a._id().equals(id)).findFirst();
        return areaOptional.map(Observable::just).orElseGet(Observable::empty);
    }

    @Override
    public Observable<List<Monster>> getMonsters(String regionId, String trainerId) {
        if (regions.isEmpty()) {
            initDummyRegions();
        }

        if (!regionId.equals(REGION_ID) || !monstersByTrainer.containsKey(trainerId)) {
            return Observable.just(List.of());
        }
        return Observable.just(List.copyOf(monstersByTrainer.get(trainerId)));
    }

    @Override
    public Observable<Monster> getMonster(String regionId, String trainer, String monsterId) {
        if (regions.isEmpty()) {
            initDummyRegions();
        }
        Monster monster = monstersById.get(monsterId);
        return monster != null ? Observable.just(monster) : Observable.error(new Throwable("404 Not found"));
    }
}
