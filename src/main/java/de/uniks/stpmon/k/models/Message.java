package de.uniks.stpmon.k.models;

public record Message(
        String createdAt,  // date-time
        String updatedAt, // date-time
        String _id,      // objectid example: 507f191e810c19729de860ea
        String sender,  // objectid example: 507f191e810c19729de860ea
        String body    // maxLength: 16384
) {

}
