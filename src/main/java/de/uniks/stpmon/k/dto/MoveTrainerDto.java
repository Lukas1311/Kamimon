package de.uniks.stpmon.k.dto;

public record MoveTrainerDto(
        String _id,    // objectid example: 507f191e810c19729de860ea
        String area, // objectid example: 507f191e810c19729de860ea
        Integer x, // coordinate x
        Integer y, // coordinate y
        Integer direction // number
) {

}
