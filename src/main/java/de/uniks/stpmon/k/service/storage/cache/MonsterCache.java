package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.RegionService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

/**
 * Cache which stores all monsters of a trainer.
 * The trainerId is set by the {@link CacheManager}.
 * <p>
 * Do not use this cache directly, use {@link CacheManager} instead.
 */
public class MonsterCache extends ListenerCache<Monster> {

    private String trainerId;
    @Inject
    protected RegionService regionService;

    @Inject
    public MonsterCache() {
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    @Override
    public ICache<Monster> init() {
        if (trainerId == null) {
            throw new IllegalStateException("TrainerId is not set");
        }
        return super.init();
    }

    @Override
    protected Observable<List<Monster>> getInitialValues() {
        return regionService.getMonsters(trainerId);
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
}
