package de.uniks.stpmon.k.world;

import java.time.LocalTime;

/**
 * Defines the day cycle of the world.
 *
 * @param dayStart   The hour of the day when the day has started
 * @param nightStart The hour of the day when the night has started
 * @param transition The hours which the transition from day to night and vice versa takes
 */
public record DayCycle(int dayStart, int nightStart, int transition) {

    /**
     * Checks if the given time is in the day.
     *
     * @param time The time to check
     * @return True if the time is in the day, false otherwise
     */
    public boolean isDay(LocalTime time) {
        return time.getHour() >= dayStart() && time.getHour() < sunset();
    }

    /**
     * Checks if the given time is between day and night.
     *
     * @param time The time to check
     * @return True if the time is between day and night, false otherwise
     */
    public boolean isSunset(LocalTime time) {
        return time.getHour() >= sunset() && time.getHour() < nightStart();
    }

    /**
     * Checks if the given time is between night and day.
     *
     * @param time The time to check
     * @return True if the time is between night and day, false otherwise
     */
    public boolean isSunrise(LocalTime time) {
        return time.getHour() >= sunrise() && time.getHour() < dayStart();
    }


    /**
     * Returns the hour of the day when the transition to night starts.
     *
     * @return The hour of the day when the transition to night starts
     */
    public int sunset() {
        return nightStart() - transition();
    }

    /**
     * Returns the hour of the day when the transition to day starts.
     *
     * @return The hour of the day when the transition to day starts
     */
    public int sunrise() {
        return dayStart() - transition();
    }
}
