package de.uniks.stpmon.k.controller.encounter;

public enum CloseEncounterTrigger {
    FLEE("you.flee"),
    WON("you.won.and.you.earn.coins"),
    END("encounter.end"),
    LOST("you.lost");

    private final String closeCause;

    CloseEncounterTrigger(final String closeCause) {
        this.closeCause = closeCause;
    }

    @Override
    public String toString() {
        return closeCause;
    }
}
