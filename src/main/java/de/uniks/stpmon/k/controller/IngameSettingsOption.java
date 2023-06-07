package de.uniks.stpmon.k.controller;

public enum IngameSettingsOption {
    MONSTER_LIST("monsterList"),
    MONSTERS("monsters"),
    MAP("map");

    private final String entryText;

    IngameSettingsOption(final String entryText) {
        this.entryText = entryText;
    }

    @Override
    public String toString() {
        return entryText;
    }
}
