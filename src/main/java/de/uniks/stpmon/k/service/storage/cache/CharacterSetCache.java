package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.world.CharacterSet;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class CharacterSetCache extends SimpleCache<CharacterSet> {
    @Inject
    protected TextureSetService textureSetService;

    @Inject
    public CharacterSetCache() {
    }

    @Override
    protected Observable<List<CharacterSet>> getInitialValues() {
        return textureSetService.createAllCharacters();
    }

    @Override
    public String getId(CharacterSet value) {
        return value.name();
    }
}
