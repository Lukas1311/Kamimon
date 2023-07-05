package de.uniks.stpmon.k.models;

public record EncounterMember(int index, int monsterIndex, boolean attacker) {
    public static final EncounterMember SELF = new EncounterMember(0, 0, false);
    public static final EncounterMember TEAM_FIRST = new EncounterMember(1, 0, false);
    public static final EncounterMember ATTACKER_FIRST = new EncounterMember(0, 0, true);
    public static final EncounterMember ATTACKER_SECOND = new EncounterMember(1, 0, true);

    public boolean isSelf() {
        return index == 0 && !attacker;
    }
}
