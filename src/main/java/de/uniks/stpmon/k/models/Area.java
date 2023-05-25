package de.uniks.stpmon.k.models;

import javax.json.Json;

public record Area(
        String _id, // objectid example: 507f191e810c19729de860ea
        String region,
        String name, //  minLength: 1, maxLength: 32
        Json map
) {

}
