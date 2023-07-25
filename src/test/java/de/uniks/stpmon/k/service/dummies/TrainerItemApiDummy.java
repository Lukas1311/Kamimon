package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.UpdateItemDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.rest.TrainerItemApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainerItemApiDummy implements TrainerItemApiService {

    final List<Item> items = new ArrayList<>();

    @Inject
    public TrainerItemApiDummy() {

    }

    private void initDummyItems() {
        if (items.size() > 0) {
            throw new IllegalStateException("Monsters already initialized");
        }

        int amount = 8;
        for (int i = 0; i < amount; i++ ) {
            int id = 100 + i;
            items.add(new Item(String.valueOf(id), null, 1, 1));
        }
    }

    @Override
    public Observable<Item> updateItem(UpdateItemDto dto, String action, String regionId, String trainerId) {
        if (regionId.isEmpty() || trainerId.isEmpty()) {
            return Observable.error(new Throwable(regionId + " or " + trainerId + " is empty"));
        }
        return null;
    }

    @Override
    public Observable<List<Item>> getItems(String regionId, String trainerId) {
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        if (items.size() == 0) {
            initDummyItems();
        }

        return Observable.just(items);
    }

    @Override
    public Observable<List<Item>> getItems(String regionId, String trainerId, List<String> types) {
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        return null;
    }

    @Override
    public Observable<Item> getItem(String regionId, String trainerId, String itemId) {
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        if (items.size() == 0) {
            initDummyItems();
        }

        Optional<Item> returnItem = items.stream()
                .filter(m -> itemId.equals(m._id()))
                .findFirst();

        return returnItem.map(r -> Observable.just(returnItem.get())).orElseGet(()
                -> Observable.error(new Throwable("404 Not found")));
    }
}
