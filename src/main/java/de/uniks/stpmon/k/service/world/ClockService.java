package de.uniks.stpmon.k.service.world;

import io.reactivex.rxjava3.core.Observable;

import java.time.LocalTime;

public class ClockService {

    public Observable<LocalTime> onTime() {
        return Observable.just(LocalTime.of(12, 30));
    }
}
