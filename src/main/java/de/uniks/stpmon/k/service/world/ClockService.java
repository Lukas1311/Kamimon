package de.uniks.stpmon.k.service.world;

import io.reactivex.rxjava3.core.Observable;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class ClockService {
    protected Observable<LocalTime> clockObservable;

    public Observable<LocalTime> onTime() {
        if (clockObservable != null) {
            return clockObservable;
        }
        clockObservable = createObservable();
        return clockObservable;
    }

    protected Observable<LocalTime> createTimer(LocalTime startTime, int period, TimeUnit unit) {
        int offsetSecond = 60 - startTime.getSecond();
        return Observable.merge(
                Observable.just(startTime),
                Observable.interval(offsetSecond, period, unit)
                        .map(ticks -> startTime.plusMinutes(ticks + 1))
        ).doOnDispose(() -> clockObservable = null).replay(1).refCount();
    }

    protected Observable<LocalTime> createObservable() {
        LocalTime currentTime = getCurrentTime();
        return createTimer(currentTime, 60, TimeUnit.SECONDS);
    }

    protected LocalTime getCurrentTime() {
        return Instant.now().atZone(ZoneId.systemDefault()).toLocalTime();
    }
}
