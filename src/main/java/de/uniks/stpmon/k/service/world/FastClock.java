package de.uniks.stpmon.k.service.world;

import io.reactivex.rxjava3.core.Observable;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class FastClock extends ClockService {
    protected Observable<LocalTime> createObservable() {
        LocalTime currentTime = LocalTime.of(4, 0);
        return createTimer(currentTime, 300, TimeUnit.MILLISECONDS);
    }
}
