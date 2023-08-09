package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.service.SettingsService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class ScalableClockService extends ClockService {
    public static final int[] STEPS = new int[]{
            1, 2, 3, 4, 5, 6, 9, 12, 48, 96, 144, 288
    };
    public static final int STEP_UNIT_IN_MINUTES = 5;
    private LocalTime lastTime = null;
    protected Observable<LocalTime> clockObservable;

    @Inject
    SettingsService settingsService;
    @Inject
    RegionStorage regionStorage;

    @Inject
    public ScalableClockService() {
    }

    public void ensureInit() {
        if (disposables.size() == 1) {
            return;
        }
        disposables.add(regionStorage.onEvents().subscribe((event) -> {
            if (event.region() != null) {
                return;
            }
            lastTime = null;
        }));
    }

    public Observable<LocalTime> onTime() {
        if (clockObservable != null) {
            return clockObservable;
        }
        ensureInit();
        clockObservable = settingsService.onDayTimeCycle().switchMap(
                        (period) -> {
                            LocalTime currentTime = lastTime != null ? lastTime : getCurrentTime();
                            float minutes = minutesFromUnit(period.intValue());
                            int delay = (int) (60_000 * (minutes / 1440));
                            return currentInterval(currentTime, delay, delay);
                        }
                ).doOnNext((value) -> lastTime = value)
                .replay(1).refCount().doOnDispose(() -> clockObservable = null);
        return clockObservable;
    }

    protected Observable<LocalTime> currentInterval(LocalTime startTime, int offsetSecond, int period) {
        return Observable.merge(
                Observable.just(startTime),
                Observable.interval(offsetSecond, period, TimeUnit.MILLISECONDS)
                        .map(ticks -> startTime.plusMinutes(ticks + 1)));
    }

    protected LocalTime getCurrentTime() {
        return Instant.now().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static int minutesFromUnit(int index) {
        return STEPS[Math.min(Math.max(index, 0), STEPS.length - 1)] * STEP_UNIT_IN_MINUTES;
    }
}
