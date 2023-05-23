package de.uniks.stpmon.k.dto;

import javax.json.Json;

public record Area(
        String createdAt, // date-time
        String updatedAt, // date-time
        String _id, // objectid example: 507f191e810c19729de860ea
        String region, 
        String name, //  minLength: 1, maxLength: 32
        Json map
    ) {
    
}
