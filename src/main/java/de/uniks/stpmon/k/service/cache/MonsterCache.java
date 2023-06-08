package de.uniks.stpmon.k.service.cache;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.RegionService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class MonsterCache extends CacheStorage<Monster> {

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
    protected String getId(Monster value) {
        return value._id();
    }
}
