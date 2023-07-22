package de.uniks.stpmon.k.controller.backpack;

public enum BackpackMenuOption {
    MONSTER("monster"),
    TEAM("team"),
    MAP("map"),
    INVENTORY("inventory");

    private final String entryText;

    BackpackMenuOption(final String entryText) {
        this.entryText = entryText;
    }

    @Override
    public String toString() {
        return entryText;
    }
}
