package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class ItemTypeCache extends LazyCache<ItemTypeDto, String> {

    @Inject
    PresetApiService presetApiService;

    @Inject
    public ItemTypeCache() {
    }

    @Override
    protected Observable<List<ItemTypeDto>> getInitialValues() {
        return presetApiService.getItems();
    }

    @Override
    public String getId(ItemTypeDto value) {
        return Integer.toString(value.id());
    }

    @Override
    protected Observable<ItemTypeDto> requestValue(String id) {
        return presetApiService.getItem(id);
    }

}
