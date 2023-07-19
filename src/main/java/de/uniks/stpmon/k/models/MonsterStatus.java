package de.uniks.stpmon.k.models;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * A possible status effect on a monster.
 * A monster can have multiple status effects at once.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MonsterStatus {
    PARALYZED("paralysed"),
    ASLEEP("asleep"),
    POISONED("poisoned"),
    BURNED("burned"),
    FROZEN("frozen"),
    CONFUSED("confused");

    private final String status;

    MonsterStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
