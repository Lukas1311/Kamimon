package de.uniks.stpmon.k.models;

public record Item(
        String createdAt,
        String updatedAt,
        String _id,
        String trainer,
        Integer type,
        Integer amount
) {

}
