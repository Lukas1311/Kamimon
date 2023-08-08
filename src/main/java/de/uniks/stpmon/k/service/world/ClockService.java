package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.service.DestructibleElement;
import io.reactivex.rxjava3.core.Observable;

import java.time.LocalTime;

public class ClockService extends DestructibleElement {

    public Observable<LocalTime> onTime() {
        return Observable.just(LocalTime.of(12, 30));
    }
}
