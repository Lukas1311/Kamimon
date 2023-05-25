package de.uniks.stpmon.k.models;

import java.util.ArrayList;

public record User(
        String _id,
        String name,
        String status,
        String avatar,
        ArrayList<String> friends) {

}
