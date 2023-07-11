package de.uniks.stpmon.k.service.world;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorldServiceTest {

    WorldService worldService = new WorldService();

    @Test
    void getNightFactor() {
        assertEquals(0, worldService.getNightFactor(LocalTime.NOON), 0.0001);
        assertEquals(0, worldService.getNightFactor(LocalTime.of(8, 0)), 0.0001);
        assertEquals(0, worldService.getNightFactor(LocalTime.of(20, 0)), 0.0001);
        assertEquals(0.3, worldService.getNightFactor(LocalTime.of(21, 0)), 0.0001);
        assertEquals(0.75, worldService.getNightFactor(LocalTime.of(23, 0)), 0.0001);
        assertEquals(1, worldService.getNightFactor(LocalTime.of(2, 0)), 0.0001);
        assertEquals(0.75, worldService.getNightFactor(LocalTime.of(5, 0)), 0.0001);
        assertEquals(0.3, worldService.getNightFactor(LocalTime.of(7, 0)), 0.0001);
        assertEquals(0, worldService.getNightFactor(LocalTime.of(8, 0)), 0.0001);
    }
}