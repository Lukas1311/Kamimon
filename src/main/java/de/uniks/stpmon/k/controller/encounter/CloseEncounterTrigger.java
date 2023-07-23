package de.uniks.stpmon.k.controller.encounter;

public enum CloseEncounterTrigger {
    FLEE("you.flee"),
    WON("you.won.you.earn.5.coins"),
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
