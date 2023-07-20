package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

public class EncounterMonsters extends SimpleCache<Monster, String> {

    @Inject
    protected EventListener listener;
    @Inject
    protected RegionService regionService;
    @Inject
    protected RegionStorage regionStorage;
    private Set<String> monsterIds;
    private Set<String> trainerIds;

    @Inject
    public EncounterMonsters() {
    }

    public void setup(Set<String> trainerIds, Set<String> monsterIds) {
        this.trainerIds = trainerIds;
        this.monsterIds = monsterIds;
    }

    @Override
    protected Observable<List<Monster>> getInitialValues() {
        String regionId = regionStorage.getRegion()._id();
        return Observable.merge(trainerIds
                .stream().map((trainer) -> regionService.getMonsters(regionId, trainer))
                .toList());
    }

    @Override
    public EncounterMonsters init() {
        super.init();
        for (String trainerId : trainerIds) {
            disposables.add(listener.listen(Socket.WS,
                    "trainers.%s.monsters.*.updated".formatted(trainerId),
                    Monster.class).subscribe(event -> {
                        final Monster value = event.data();
                        if (!isCacheable(value)) {
                            return;
                        }
                        updateValue(value);
                    }
            ));
        }
        return this;
    }

    @Override
    public String getId(Monster value) {
        return value._id();
    }

    @Override
    protected boolean isCacheable(Monster value) {
        return monsterIds.contains(value._id());
    }
}
