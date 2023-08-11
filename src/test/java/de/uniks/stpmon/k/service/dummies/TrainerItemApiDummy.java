package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.UpdateItemDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.rest.TrainerItemApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class TrainerItemApiDummy implements TrainerItemApiService {

    final List<Item> items = new ArrayList<>();

    @Inject
    RegionApiDummy regionApiDummy;
    @Inject
    EventDummy eventDummy;

    @Inject
    public TrainerItemApiDummy() {

    }

    private void initDummyItems() {
        if (!items.isEmpty()) {
            throw new IllegalStateException("Items already initialized");
        }

        int amount = 8;
        for (int i = 0; i < amount; i++) {
            Item item = new Item(String.valueOf(i), null, 1, 1);
            if (i == 2) {
                //Add ItemBox
                item = new Item(String.valueOf(i), "0", 2, 1);
            } else if (i == 3) {
                //Add MonBox
                item = new Item(String.valueOf(i), "0", 3, 1);
            } else if (i == 4) {
                //Add MonBall
                item = new Item(String.valueOf(i), null, 4, 1);
            }
            items.add(item);
        }
    }

    @Override
    public Observable<Item> updateItem(String regionId, String trainerId, String action, UpdateItemDto dto) {
        if (regionId.isEmpty() || trainerId.isEmpty() || regionApiDummy == null) {
            return Observable.error(new Throwable(regionId + " or " + trainerId + " is empty"));
        }
        Item item = new Item("", "", 0, 0);
        if (dto.type().equals(2)) {
            item = new Item("2", "0", 2, 0);
            eventDummy.sendEvent(new Event<>("trainers.%s.items.%s.updated".formatted(trainerId, "2"), item));
        }
        if (dto.type().equals(3)) {
            item = new Item("3", "0", 3, 0);
            eventDummy.sendEvent(new Event<>("trainers.%s.items.%s.updated".formatted(trainerId, "3"), item));
        }

        return Observable.just(item);
    }


    @Override
    public Observable<List<Item>> getItems(String regionId, String trainerId) {
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        if (items.isEmpty()) {
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
        if (items.isEmpty()) {
            initDummyItems();
        }

        Optional<Item> returnItem = items.stream()
                .filter(m -> itemId.equals(m._id()))
                .findFirst();

        return returnItem.map(r -> Observable.just(returnItem.get())).orElseGet(()
                -> Observable.error(new Throwable("404 Not found")));
    }
}
