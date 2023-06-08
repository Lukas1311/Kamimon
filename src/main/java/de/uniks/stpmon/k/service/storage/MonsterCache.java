package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Monster;

import javax.inject.Inject;

public class MonsterCache extends CacheStorage<Monster> {

    private String trainerId;

    @Inject
    public MonsterCache() {
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
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
