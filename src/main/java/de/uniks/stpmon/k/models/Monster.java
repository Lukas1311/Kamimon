package de.uniks.stpmon.k.models;

import java.util.List;
import java.util.SortedMap;

/**
 * @param _id               objectid example: 507f191e810c19729de860ea
 * @param trainer           objectid example: 507f191e810c19729de860ea
 * @param type              Id of the monster type
 * @param level             The current level of the monster
 * @param experience        The current experience of the monster
 * @param abilities         object whose keys are the currently known ability IDs and values are remaining uses,
 *                          maxProperties: 4, example: { "1": 15, "2": 10, "7": 5, "10": 0 }
 * @param attributes        The persistent or maximum attributes of the monster.
 * @param currentAttributes The current attributes of the monster. Resets to persistent attributes when healed.
 * @param status            List of status effects on the monster.
 */
public record Monster(
        String _id,
        String trainer,
        Integer type,
        Integer level,
        Integer experience,
        SortedMap<String, Integer> abilities,
        MonsterAttributes attributes,
        MonsterAttributes currentAttributes,
        List<MonsterStatus> status
) {

}
