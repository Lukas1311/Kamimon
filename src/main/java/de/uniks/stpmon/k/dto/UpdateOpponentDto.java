package de.uniks.stpmon.k.dto;

public record UpdateOpponentDto(
        String monster,

        IMove move

) implements IMoves {

}
