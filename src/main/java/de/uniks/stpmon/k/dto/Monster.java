package de.uniks.stpmon.k.dto;

import java.util.SortedMap;

public record Monster(
    String createdAt, // date-time 
    String updatedAt, // date-time 
    String _id, // objectid example: 507f191e810c19729de860ea
    String trainer, // objectid example: 507f191e810c19729de860ea
    Integer type,
    Integer level,
    Integer experience,
    // object whose keys are the currently known ability IDs and values are remaining uses
    SortedMap<String, Integer> abilities, // maxProperties: 4, example: { "1": 15, "2": 10, "7": 5, "10": 0 }
    MonsterAttributes attributes, // The persistent or maximum attributes of the monster.
    MonsterAttributes currentAttributes // The current attributes of the monster. Resets to persistent attributes when healed.
) {

}
