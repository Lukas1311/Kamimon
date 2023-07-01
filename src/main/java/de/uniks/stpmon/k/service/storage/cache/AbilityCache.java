package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class AbilityCache extends LazyCache<AbilityDto, String> {

    @Inject
    PresetApiService presetApiService;

    @Inject
    public AbilityCache() {
    }

    @Override
    protected Observable<List<AbilityDto>> getInitialValues() {
        return presetApiService.getAbilities();
    }

    @Override
    public String getId(AbilityDto value) {
        return Integer.toString(value.id());
    }

    @Override
    protected Observable<AbilityDto> requestValue(String id) {
        return presetApiService.getAbility(id);
    }

}
