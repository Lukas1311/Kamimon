package de.uniks.stpmon.k.dto;

public record AbilityMove(

        String type, // enum [ ability ]

        Integer ability,

        String target // Opponent ID

) implements IMove {

}
