package de.uniks.stpmon.k.dto;

public record ChangeMonsterMove(

        String type, // enum [ change-monster ]

        String monster // Monster ID

) implements IMove {

}
