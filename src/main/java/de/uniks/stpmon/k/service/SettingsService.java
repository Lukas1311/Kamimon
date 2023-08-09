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
    private final BehaviorSubject<Boolean> nightMode = BehaviorSubject.createDefault(true);
    private final BehaviorSubject<Boolean> soundMuted = BehaviorSubject.createDefault(false);
    private final BehaviorSubject<Float> soundValue = BehaviorSubject.createDefault(100f);

    @Inject
    public SettingsService() {
    }

    private void ensureInit() {
        if (initialized) {
            return;
        }
        nightMode.onNext(preferences.getBoolean("nightMode", true));
        soundMuted.onNext(preferences.getBoolean("soundMuted", false));
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
        preferences.putBoolean("nightMode", value);
        nightMode.onNext(value);
    }

    public void setSoundMuted(boolean value) {
        ensureInit();
        preferences.putBoolean("soundMuted", value);
        soundMuted.onNext(value);
    }

    public Float getSoundValue() {
        ensureInit();
        return soundValue.getValue();
    }

    public Boolean getNightEnabled() {
        ensureInit();
        return nightMode.getValue();
    }

    public Boolean getSoundMuted() {
        ensureInit();
        return soundMuted.getValue();
    }

    public Observable<Boolean> onNightModusEnabled() {
        ensureInit();
        return nightMode;
    }

    public Observable<Boolean> onSoundMuted() {
        ensureInit();
        return soundMuted;
    }

    public Observable<Float> onSoundValue() {
        ensureInit();
        return soundValue;
    }
}
