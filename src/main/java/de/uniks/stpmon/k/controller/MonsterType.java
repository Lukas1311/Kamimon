package de.uniks.stpmon.k.controller;

public enum MonsterType {

    NORMAL("normal", "monster-type-normal"),
    FIRE("fire", "monster-type-fire"),
    FAIRY("fairy", "monster-type-fairy"),
    WATER("water", "monster-type-water"),
    ELECTRIC("electric", "monster-type-electric"),
    GRASS("grass", "monster-type-grass"),
    ICE("ice", "monster-type-ice"),
    GROUND("ground", "monster-type-ground"),
    FLYING("flying", "monster-type-flying"),
    GHOST("ghost", "monster-type-ghost"),
    ROCK("rock", "monster-type-rock"),
    FIGHTING("fighting", "monster-type-fighting"),
    POISON("poison", "monster-type-poison"),
    PSYCHIC("psychic", "monster-type-psychic"),
    BUG("bug", "monster-type-bug"),
    DARK("dark", "monster-type-dark"),
    STEEL("steel", "monster-type-steel"),
    DRAGON("dragon", "monster-type-dragon");

    private final String typeName;
    private final String styleClass;

    MonsterType(final String typeName, final String styleClass){
        this.typeName = typeName;
        this.styleClass = styleClass;
    }

    @Override
    public String toString(){
        return typeName;
    }

    public String getStyleClass(){
        return styleClass;
    }

    public String getTypeName(){
        return typeName;
    }
}
