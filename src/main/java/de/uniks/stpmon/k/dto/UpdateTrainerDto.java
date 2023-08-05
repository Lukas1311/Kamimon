package de.uniks.stpmon.k.dto;

import java.util.List;

public record UpdateTrainerDto(
        String name,
        String image,
        List<String> team,
        String area
) {

}

