package de.uniks.stpmon.k.models;

import javax.json.Json;

public record Region(
        String _id,
        String name,
        Spawn spawn,
        Json map // Tiled map in JSON format
        // TODO: create own record for tiled map ? with custom values that we need
) {
}
