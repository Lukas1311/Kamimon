package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Opponent;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class OpponentCache extends ListenerCache<Opponent, String> {

    protected String encounterId;
    private List<Opponent> initialOpponents;

    @Inject
    public OpponentCache() {
    }

    public void setup(String encounterId, List<Opponent> initialOpponents) {
        this.encounterId = encounterId;
        this.initialOpponents = initialOpponents;
    }

    @Override
    protected Class<? extends Opponent> getDataClass() {
        return Opponent.class;
    }

    @Override
    protected String getEventName() {
        return "encounters.%s.trainers.*.opponents.*.*".formatted(
                encounterId
        );
    }

    @Override
    protected Observable<List<Opponent>> getInitialValues() {
        return Observable.just(initialOpponents);
    }

    @Override
    public String getId(Opponent value) {
        return value._id();
    }

}
