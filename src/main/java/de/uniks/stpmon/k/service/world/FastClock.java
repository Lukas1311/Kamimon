package de.uniks.stpmon.k.service.world;

import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Singleton
public class FastClock extends ClockService {

    protected Observable<LocalTime> createObservable() {
        LocalTime currentTime = LocalTime.of(4, 0);
        return createTimer(currentTime, 100, TimeUnit.MILLISECONDS);
    }
}
