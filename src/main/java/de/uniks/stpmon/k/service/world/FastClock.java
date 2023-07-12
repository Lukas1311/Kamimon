package de.uniks.stpmon.k.service.world;

import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Singleton
public class FastClock extends ClockService {

    @Inject
    public FastClock() {
    }

    protected Observable<LocalTime> createObservable() {
        LocalTime currentTime = LocalTime.of(18, 0);
        int offsetSecond = 60 - currentTime.getSecond();
        return Observable.merge(
                Observable.just(currentTime),
                Observable.interval(offsetSecond, 100, TimeUnit.MILLISECONDS)
                        .map(ticks -> currentTime.plus(ticks + 1, ChronoUnit.MINUTES))
        ).doOnDispose(() -> clockObservable = null).replay(1).refCount();
    }
}
