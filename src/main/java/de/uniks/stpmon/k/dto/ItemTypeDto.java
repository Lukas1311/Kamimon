package de.uniks.stpmon.k.dto;

import de.uniks.stpmon.k.models.ItemUse;

public record ItemTypeDto(
        Integer id,
        String image,
        String name,
        Integer price,
        String description,
        ItemUse use
) {

}
