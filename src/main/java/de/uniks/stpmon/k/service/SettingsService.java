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
    private final BehaviorSubject<Float> dayTimeCycle = BehaviorSubject.createDefault(12f);
    private final BehaviorSubject<Integer> currentSong = BehaviorSubject.createDefault(0);

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
        currentSong.onNext(preferences.getInt("currentSong", 0));
        dayTimeCycle.onNext(preferences.getFloat("dayTimeCycle", 12f));
        initialized = true;
    }

    public void setCurrentSong(int index) {
        preferences.putInt("currentSong", index);
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

    public boolean setDayTimeCycle(float value) {
        int hour = (int) value;
        int minute = (int) ((value - hour) * 60);
        int lastHour = getDayTimeCycle().intValue();
        int lastMinute = (int) ((getDayTimeCycle() - lastHour) * 60);
        if (hour == lastHour && minute == lastMinute) {
            return false;
        }
        ensureInit();
        preferences.putFloat("dayTimeCycle", value);
        dayTimeCycle.onNext(value);
        return true;
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

    public Float getDayTimeCycle() {
        ensureInit();
        return dayTimeCycle.getValue();
    }

    public Observable<Boolean> onNightModeEnabled() {
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

    public Observable<Float> onDayTimeCycle() {
        ensureInit();
        return dayTimeCycle;
    }
}
