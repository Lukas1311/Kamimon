package de.uniks.stpmon.k.models;

/**
 * Represents a slot of an encounter.
 *
 * @param partyIndex The index of the member in the current party of the encounter
 * @param enemy      True if the member is an enemy, false if it is a defender
 */
public record EncounterSlot(int partyIndex, boolean enemy) {
    public static final EncounterSlot PARTY_FIRST = new EncounterSlot(0, false);
    public static final EncounterSlot PARTY_SECOND = new EncounterSlot(1, false);
    public static final EncounterSlot ENEMY_FIRST = new EncounterSlot(0, true);
    public static final EncounterSlot ENEMY_SECOND = new EncounterSlot(1, true);
}
