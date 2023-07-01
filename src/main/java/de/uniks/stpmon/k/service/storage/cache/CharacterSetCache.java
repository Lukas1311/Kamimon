package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.world.CharacterSet;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class CharacterSetCache extends LazyCache<CharacterSet, String> {

    @Inject
    protected IResourceService resourceService;

    @Inject
    public CharacterSetCache() {
    }

    @Override
    protected Observable<List<CharacterSet>> getInitialValues() {
        // Loaded via the preparation service
        return Observable.empty();
    }

    @Override
    protected Observable<CharacterSet> requestValue(String id) {
        return resourceService.getCharacterImage(id)
                .map((body) -> new CharacterSet(id, body));
    }

    @Override
    public String getId(CharacterSet value) {
        return value.name();
    }

}
