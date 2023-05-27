package de.uniks.stpmon.k.dto;

import java.util.List;

public record MonsterTypeDto(
        Integer id,
        String name,
        String image,
        List<String> type, // array because of multi-types
        String description
) {

}
