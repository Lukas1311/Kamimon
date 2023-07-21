package de.uniks.stpmon.k.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A possible status effect on a monster.
 * A monster can have multiple status effects at once.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MonsterStatus {
    @JsonProperty("paralysed")
    PARALYZED("paralysed"),
    @JsonProperty("asleep")
    ASLEEP("asleep"),
    @JsonProperty("poisoned")
    POISONED("poisoned"),
    //TODO: Remove, if server status is fixed
    @JsonProperty("burn")
    BURNED("burned"),
    @JsonProperty("burned")
    BURN("burned"),
    @JsonProperty("frozen")
    FROZEN("frozen"),
    @JsonProperty("confused")
    CONFUSED("confused"),
    //TODO: Remove, if server status is fixed
    @JsonProperty("confusion")
    CONFUSION("confused");

    private final String status;

    MonsterStatus(String status) {
        this.status = status;
    }

    public String getIconName() {
        return "status/" + status + "_icon.png";
    }

    @Override
    public String toString() {
        return status;
    }
}
