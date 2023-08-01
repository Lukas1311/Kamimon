package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.service.SettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorldServiceTest {

    @InjectMocks
    WorldService worldService;
    @Mock
    protected SettingsService settingsService;

    @Test
    void getNightFactor() {
        when(settingsService.getNightEnabled()).thenReturn(true);
        assertFalse(worldService.getNightFactor(LocalTime.of(19, 50)) > 0);
        assertEquals(0, worldService.getNightFactor(LocalTime.of(20, 0)), 0.0001);
        assertTrue(worldService.getNightFactor(LocalTime.of(20, 10)) > 0);
        assertEquals(1, worldService.getNightFactor(LocalTime.of(21, 0)), 0.0001);
        assertEquals(1, worldService.getNightFactor(LocalTime.of(23, 0)), 0.0001);
        assertEquals(1, worldService.getNightFactor(LocalTime.of(2, 0)), 0.0001);
        assertEquals(1, worldService.getNightFactor(LocalTime.of(5, 0)), 0.0001);
        assertEquals(1, worldService.getNightFactor(LocalTime.of(7, 0)), 0.0001);
        assertTrue(worldService.getNightFactor(LocalTime.of(7, 0)) - worldService.getNightFactor(LocalTime.of(7, 50)) > 0);
        assertEquals(0, worldService.getNightFactor(LocalTime.of(8, 0)), 0.0001);
        assertFalse(worldService.getNightFactor(LocalTime.of(8, 10)) > 0);

        // disable night
        when(settingsService.getNightEnabled()).thenReturn(false);
        // should always be 0 if night is disabled
        assertEquals(0, worldService.getNightFactor(LocalTime.of(21, 0)));
    }

}