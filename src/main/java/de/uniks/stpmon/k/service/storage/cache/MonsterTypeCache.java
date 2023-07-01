package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class MonsterTypeCache extends LazyCache<MonsterTypeDto, String> {

    @Inject
    PresetApiService presetApiService;

    @Inject
    public MonsterTypeCache() {
    }

    @Override
    protected Observable<List<MonsterTypeDto>> getInitialValues() {
        return presetApiService.getMonsters();
    }

    @Override
    public String getId(MonsterTypeDto value) {
        return String.valueOf(value.id());
    }

    @Override
    protected Observable<MonsterTypeDto> requestValue(String id) {
        return presetApiService.getMonster(id);
    }

}
