package de.uniks.stpmon.k.service.world;

import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class FastClock extends ClockService {
    @Inject
    public FastClock() {

    }

    protected Observable<LocalTime> createObservable() {
        LocalTime currentTime = LocalTime.of(4, 0);
        return createTimer(currentTime, 125, TimeUnit.MILLISECONDS);
    }
}
