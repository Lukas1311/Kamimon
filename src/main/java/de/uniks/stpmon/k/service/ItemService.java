package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.UpdateItemDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.rest.TrainerItemApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheProxy;
import de.uniks.stpmon.k.service.storage.cache.ItemCache;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.Optional;

public class ItemService {

    @Inject
    Provider<ItemCache> itemCacheProvider;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    TrainerItemApiService trainerItemApiService;
    @Inject
    TrainerService trainerService;

    @Inject
    public ItemService() {

    }

    private final CacheProxy<ItemCache, Item, String> itemCache = new CacheProxy<>(() -> itemCacheProvider, (c) -> {
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return;
        }
        c.setTrainerId(trainer._id());
    });

    public Observable<List<Item>> getItems() {
        return itemCache.getValues();
    }

    public Observable<Optional<Item>> getItem(String id) {
        return itemCache.listenValue(id);
    }

    public Observable<Item> tradeItem(int itemType, int tradeAmount, String targetId, boolean sellItem) {
        UpdateItemDto update = new UpdateItemDto((sellItem? -1 : 1) * tradeAmount, itemType, targetId);
        return trainerItemApiService.updateItem(trainerService.getMe().region(), trainerService.getMe()._id(), "trade", update);
    }

}
