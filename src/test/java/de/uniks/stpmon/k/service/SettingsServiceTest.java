package de.uniks.stpmon.k.service;

import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettingsServiceTest {

    @Mock
    Preferences preferences;

    @InjectMocks
    SettingsService settingsService;

    @Test
    public void testSound() {
        when(preferences.getFloat("soundValue", 100f)).thenReturn(100f);
        TestObserver<Float> testObserver = settingsService.onSoundValue().test();
        // check initial value
        assertEquals(100f, settingsService.getSoundValue());
        testObserver.assertValueAt(0, 100f);

        // set value to 50
        settingsService.setSoundValue(50f);
        // Check if update was called
        testObserver.assertValueAt(1, 50f);
        // check if preferences were updated
        verify(preferences).putFloat("soundValue", 50f);
        // Check if value were set
        assertEquals(50f, settingsService.getSoundValue());
    }

    @Test
    public void testNight() {
        when(preferences.getBoolean("nightMode", true)).thenReturn(true);
        TestObserver<Boolean> testObserver = settingsService.onNightModeEnabled().test();
        // check initial value
        assertEquals(true, settingsService.getNightEnabled());
        testObserver.assertValueAt(0, true);

        // set value to false
        settingsService.setNightEnabled(false);
        // Check if update was called
        testObserver.assertValueAt(1, false);
        // check if preferences were updated
        verify(preferences).putBoolean("nightMode", false);
        // Check if value were set
        assertEquals(false, settingsService.getNightEnabled());
    }

    @Test
    public void testCycle() {
        when(preferences.getFloat("soundValue", 100f)).thenReturn(100f);
        when(preferences.getFloat("dayTimeCycle", 12)).thenReturn(12f);
        TestObserver<Float> testObserver = settingsService.onDayTimeCycle().test();
        // check initial value
        assertEquals(12f, settingsService.getDayTimeCycle());
        testObserver.assertValueAt(0, 12f);

        // set value to false
        settingsService.setDayTimeCycle(1f);
        // Check if update was called
        testObserver.assertValueAt(1, 1f);
        // check if preferences were updated
        verify(preferences).putFloat("dayTimeCycle", 1f);
        // Check if value were set
        assertEquals(1f, settingsService.getDayTimeCycle());
    }
}
