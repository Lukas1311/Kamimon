package de.uniks.stpmon.k.dto;

public record UpdateOpponentDto(
        String monster,

        AbilityMove abilityMove,

        ChangeMonsterMove changeMonsterMove

) implements IMoves {
}
