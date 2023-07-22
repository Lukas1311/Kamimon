package de.uniks.stpmon.k.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ItemUse {

    @JsonProperty("ball")
    BALL("ball"),
    @JsonProperty("effect")
    EFFECT("effect"),
    @JsonProperty("itemBox")
    ITEM_BOX("itemBox"),
    @JsonProperty("monsterBox")
    MONSTER_BOX("monsterBox");

    private final String use;

    ItemUse(String use) {
        this.use = use;
    }

    @Override
    public String toString(){
        return use;
    }
}
