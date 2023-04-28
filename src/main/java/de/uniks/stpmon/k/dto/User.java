package de.uniks.stpmon.k.dto;

import java.lang.reflect.Array;
import java.util.ArrayList;

public record User(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String status,
        String avatar,
        ArrayList<String> friends
        ) {

}
