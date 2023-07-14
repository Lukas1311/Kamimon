package de.uniks.stpmon.k.service.world;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class WorldServiceTest {

    final WorldService worldService = new WorldService();

    @Test
    void getNightFactor() {
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
    }
}