package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.rest.TrainerItemApiService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class ItemCache extends ListenerCache<Item, String> {

    private String trainerId;
    @Inject
    protected TrainerItemApiService itemApiService;
    @Inject
    protected RegionStorage regionStorage;

    @Inject
    public ItemCache() {
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    @Override
    public ICache<Item, String> init() {
        if (trainerId == null) {
            throw new IllegalStateException("TrainerId is not set");
        }
        if (regionStorage == null || regionStorage.getRegion() == null) {
            throw new IllegalStateException("Region is not set");
        }
        return super.init();
    }

    @Override
    protected Observable<List<Item>> getInitialValues() {
        return itemApiService.getItems(regionStorage.getRegion()._id(), trainerId);
    }

    @Override
    protected Class<? extends Item> getDataClass() {
        return Item.class;
    }

    @Override
    protected String getEventName() {
        return String.format("trainers.%s.items.*.*", trainerId);
    }

    @Override
    public String getId(Item value) {
        return String.valueOf(value.type());
    }
}
