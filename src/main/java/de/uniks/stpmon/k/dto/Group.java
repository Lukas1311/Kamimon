package de.uniks.stpmon.k.dto;

import java.util.ArrayList;

public record Group(
    String createdAt,  // date-time
    String updatedAt, // date-time
    String _id,      // objectid example: 507f191e810c19729de860ea
    String name,    // minLength: 1, maxLength: 32
    ArrayList<String> members    // maxItems: 100
) {
    
}
