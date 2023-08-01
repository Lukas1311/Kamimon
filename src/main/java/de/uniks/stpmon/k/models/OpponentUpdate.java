package de.uniks.stpmon.k.models;

public record OpponentUpdate(
        EncounterSlot slot,
        Opponent opponent,
        Opponent lastOpponent
) {
}
