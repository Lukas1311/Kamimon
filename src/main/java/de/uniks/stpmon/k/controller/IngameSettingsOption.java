package de.uniks.stpmon.k.controller;

public enum IngameSettingsOption {
    MONSTER_LIST("MonsterList"),
    MONSTERS("Monsters"),
    MAP("Map");

    private final String entryText;

    IngameSettingsOption(final String entryText) {
        this.entryText = entryText;
    }

    @Override
    public String toString() {
        return entryText;
    }
}
