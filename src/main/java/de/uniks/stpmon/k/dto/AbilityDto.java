package de.uniks.stpmon.k.dto;

public record AbilityDto(
        Integer id,
        String name,
        String description,
        String type,
        Integer maxUses,
        Number accuracy, // min: 0, max: 1, The highest chance of any effect: 1 -> 100 %
        Integer power // min: 0, The amount of damage this ability does
) {

}
