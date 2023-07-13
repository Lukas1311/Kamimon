package de.uniks.stpmon.k.controller.encounter;

public enum CloseEncounter {
    FLEE("you.flee"),
    WON("you.won"),
    LOST("you.lost");

    private final String closeCause;

    CloseEncounter(final String closeCause) {
        this.closeCause = closeCause;
    }

    @Override
    public String toString() {
        return closeCause;
    }
}
