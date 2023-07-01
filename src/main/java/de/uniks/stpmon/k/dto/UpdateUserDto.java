package de.uniks.stpmon.k.dto;

import java.util.ArrayList;

public record UpdateUserDto(
        String name,
        String status,
        String avatar,
        ArrayList<String> friends,
        String password
) {

}
