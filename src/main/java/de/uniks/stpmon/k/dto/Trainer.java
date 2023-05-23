package de.uniks.stpmon.k.dto;

public record Trainer(
        String createdAt,	// date-time
        String updatedAt,	// date-time
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
        NPCInfo npc
    ) {
    
}
