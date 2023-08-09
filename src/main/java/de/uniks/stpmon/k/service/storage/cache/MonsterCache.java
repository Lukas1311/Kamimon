package de.uniks.stpmon.k.service.storage.cache;

import dagger.internal.Preconditions;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.*;

/**
 * Cache which stores all monsters of a trainer.
 * The trainerId is set by the {@link CacheManager}.
 * <p>
 * Do not use this cache directly, use {@link CacheManager} instead.
 */
public class MonsterCache extends ListenerCache<Monster, String> {

    private String trainerId;
    @Inject
    protected RegionService regionService;
    @Inject
    protected RegionStorage regionStorage;
    @Inject
    protected CacheManager cacheManager;
    protected ICache<Trainer, String> trainerCache;
    private TeamCache teamCache;

    @Inject
    public MonsterCache() {
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    @Override
    public ICache<Monster, String> init() {
        if (trainerId == null) {
            throw new IllegalStateException("TrainerId is not set");
        }
        if (regionStorage == null || regionStorage.getRegion() == null) {
            throw new IllegalStateException("Region is not set");
        }
        trainerCache = cacheManager.trainerCache();
        teamCache = new TeamCache(this);
        childCaches.add(teamCache);
        teamCache.init();
        return super.init();
    }

    @Override
    protected Observable<List<Monster>> getInitialValues() {
        return regionService.getMonsters(regionStorage.getRegion()._id(), trainerId);
    }

    @Override
    protected Class<? extends Monster> getDataClass() {
        return Monster.class;
    }

    @Override
    protected String getEventName() {
        return String.format("trainers.%s.monsters.*.*", trainerId);
    }

    @Override
    public String getId(Monster value) {
        return value._id();
    }

    public ICache<Monster, String> getTeam() {
        return teamCache;
    }

    private static class TeamCache extends SimpleCache<Monster, String> {

        private final MonsterCache parent;
        private List<String> currentTeam = null;

        public TeamCache(MonsterCache parent) {
            this.parent = parent;
        }

        @Override
        public ICache<Monster, String> init() {
            // listen to trainer changes and update monster cache
            disposables.add(parent.trainerCache.listenValue(parent.trainerId)
                    .map(trainer -> trainer.map(Trainer::team).orElse(List.of()))
                    .subscribe(this::updateTeam));
            return super.init();
        }

        private void updateTeam(List<String> team) {
            // Check if cache is still valid
            if (parent.trainerCache == null
                    || parent.trainerCache.isDestroyed()) {
                return;
            }
            List<String> teamIds = team;
            if (currentTeam == null) {
                currentTeam = new LinkedList<>(teamIds);
                addValues(parent.valuesById.values());
                return;
            }
            if (currentTeam.equals(teamIds)) {
                return;
            }
            teamIds = new LinkedList<>(teamIds);
            List<String> oldTeam = currentTeam;

            Set<String> removedMonsters = new HashSet<>(oldTeam);
            teamIds.forEach(removedMonsters::remove);

            Set<String> addedMonsters = new HashSet<>(teamIds);
            oldTeam.forEach(addedMonsters::remove);

            // Disable subject to prevent unneeded events
            disableEvents();
            // Remove values from cache
            for (String monsterId : removedMonsters) {
                getValue(monsterId).ifPresent(this::removeValue);
            }

            currentTeam = teamIds;

            // Add values to cache from the parent cache
            for (String monsterId : addedMonsters) {
                parent.getValue(monsterId).ifPresent(this::addValue);
            }
            // Re-enable subject
            enableEvents();

            List<Monster> monsters = new LinkedList<>();
            for (String id : currentTeam) {
                Optional<Monster> monster = getValue(id);
                if (monster.isEmpty()) {
                    continue;
                }
                monsters.add(monster.get());
            }

            // Emit new values
            subject.onNext(new ArrayList<>(monsters));
        }

        @Override
        protected Observable<List<Monster>> getInitialValues() {
            return parent.onInitialized().andThen(parent.getValues()
                    .skip(1)
                    .take(1));
        }

        @Override
        protected boolean isCacheable(Monster value) {
            Preconditions.checkNotNull(currentTeam,
                    "Null check failed probably because init() was not called");
            return currentTeam.contains(value._id());
        }

        @Override
        public String getId(Monster value) {
            return value._id();

        }

    }

}
