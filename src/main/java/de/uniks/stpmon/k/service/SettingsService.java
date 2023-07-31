package de.uniks.stpmon.k.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.prefs.Preferences;

@Singleton
public class SettingsService {
    @Inject
    Preferences preferences;
    boolean initialized = false;
    private final BehaviorSubject<Boolean> nightModus = BehaviorSubject.createDefault(true);
    private final BehaviorSubject<Float> soundValue = BehaviorSubject.createDefault(100f);

    @Inject
    public SettingsService() {
    }

    private void ensureInit() {
        if (initialized) {
            return;
        }
        nightModus.onNext(preferences.getBoolean("nightModus", true));
        soundValue.onNext(preferences.getFloat("soundValue", 100f));
        initialized = true;
    }

    public void setSoundValue(float value) {
        ensureInit();
        preferences.putFloat("soundValue", value);
        soundValue.onNext(value);
    }

    public void setNightEnabled(boolean value) {
        ensureInit();
        preferences.putBoolean("nightModus", value);
        nightModus.onNext(value);
    }

    public Float getSoundValue() {
        ensureInit();
        return soundValue.getValue();
    }

    public Boolean getNightEnabled() {
        ensureInit();
        return nightModus.getValue();
    }

    public Observable<Boolean> onNightModusEnabled() {
        ensureInit();
        return nightModus;
    }

    public Observable<Float> onSoundValue() {
        ensureInit();
        return soundValue;
    }
}
