package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class MonsterTypeCache extends SimpleCache<MonsterTypeDto, String> {

    @Inject
    protected PresetService presetService;

    @Inject
    public MonsterTypeCache() {
    }

    @Override
    protected Observable<List<MonsterTypeDto>> getInitialValues() {
        return presetService.getMonsters();
    }

    @Override
    public String getId(MonsterTypeDto value) {
        return String.valueOf(value.id());
    }
}
