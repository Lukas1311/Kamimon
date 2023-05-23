package de.uniks.stpmon.k.dto;

import java.util.ArrayList;

public record CreateUserDto(
        String name,
        String avatar,
        String password) {
}
