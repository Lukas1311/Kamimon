package de.uniks.stpmon.k.dto;

import java.util.ArrayList;

public record User(
        String createdAt, // date-time
        String updatedAt, // date-time
        String _id,
        String name,
        String status,
        String avatar,
        ArrayList<String> friends
        ) {

}
