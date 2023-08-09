package de.uniks.stpmon.k.dto;

public record UseItemMove(
        String type, //enum [use-item]
        Integer item,   //item type
        String target) //target ID
        implements IMove {

}

