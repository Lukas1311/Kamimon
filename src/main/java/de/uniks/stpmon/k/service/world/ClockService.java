package de.uniks.stpmon.k.service.world;

import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Singleton
public class ClockService {

    @Inject
    public ClockService() {
    }

    protected Observable<LocalTime> clockObservable;

    public Observable<LocalTime> onTime() {
        if (clockObservable != null) {
            return clockObservable;
        }
        clockObservable = createObservable();
        return clockObservable;
    }

    protected Observable<LocalTime> createObservable() {
        LocalTime currentTime = getCurrentTime();
        return Observable.merge(
                Observable.just(currentTime),
                Observable.interval(60, TimeUnit.SECONDS)
                        .map(ticks -> currentTime.plus(ticks + 1, ChronoUnit.MINUTES))
        ).doOnDispose(() -> {
            clockObservable = null;
        }).replay(1).refCount();
    }

    private LocalTime getCurrentTime() {
        return Instant.now().atZone(ZoneId.systemDefault()).toLocalTime();
    }
}
