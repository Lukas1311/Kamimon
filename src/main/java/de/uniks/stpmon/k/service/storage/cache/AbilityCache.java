package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class AbilityCache extends SimpleCache<AbilityDto> {
    @Inject
    protected PresetService presetService;

    @Inject
    public AbilityCache() {
    }

    @Override
    protected Observable<List<AbilityDto>> getInitialValues() {
        return presetService.getAbilities();
    }

    @Override
    public String getId(AbilityDto value) {
        return Integer.toString(value.id());
    }
}
