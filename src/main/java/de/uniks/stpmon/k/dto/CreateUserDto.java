package de.uniks.stpmon.k.dto;

public record CreateUserDto(
        String name,
        String avatar,
        String password
) {

}
