package de.uniks.stpmon.k.models;

import java.util.List;
import java.util.Set;

public record Trainer(
        String _id, // objectid example: 507f191e810c19729de860ea
        String region, // objectid example: 507f191e810c19729de860ea
        String user, // objectid example: 507f191e810c19729de860ea
        String name,
        String image,
        Integer coins,
        String area, // objectid example: 507f191e810c19729de860ea
        Integer x,
        Integer y,
        Integer direction,
        NPCInfo npc,
        List<String> team, // up to 6 ids
        Set<Integer> encounteredMonsterTypes,
        Set<String> visitedAreas) {
}
