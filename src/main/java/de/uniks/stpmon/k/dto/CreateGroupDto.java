package de.uniks.stpmon.k.dto;

import java.util.List;

public record CreateGroupDto(
        String name, // minLength: 1, maxLength: 32
        List<String> members // maxItems: 100
) {

}
