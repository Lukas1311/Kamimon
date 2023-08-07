package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.service.SettingsService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class ScalableClockService extends ClockService {
    public static final int[] STEPS = new int[]{
            1, 2, 3, 4, 5, 6, 9, 12, 48, 96, 144, 288
    };
    public static final int STEP_UNIT_IN_MINUTES = 5;
    @Inject
    SettingsService settingsService;

    @Inject
    public ScalableClockService() {
    }

    @Override
    protected Observable<LocalTime> createObservable() {
        return settingsService.onDayTimeCycle().flatMap((period) -> {
                    LocalTime currentTime = getCurrentTime();
                    int hours = (int) (period / 3);
                    float fac = hours + (period % 3) / 3;
                    return createTimer(currentTime, (int) (60 / (12f / fac)), TimeUnit.SECONDS);
                }

        );
    }
}
