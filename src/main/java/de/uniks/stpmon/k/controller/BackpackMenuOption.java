package de.uniks.stpmon.k.controller;

public enum BackpackMenuOption {
    MONSTER_LIST("monsterList"),
    MONSTERS("monsters"),
    MAP("map");

    private final String entryText;

    BackpackMenuOption(final String entryText) {
        this.entryText = entryText;
    }

    @Override
    public String toString() {
        return entryText;
    }
}