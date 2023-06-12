package de.uniks.stpmon.k.utils;

public class UiToggle {
    private boolean value;

    public UiToggle(boolean initialValue) {
        this.value = initialValue;
    }

    public boolean toggle() {
        value = !value;
        return value;
    }
}
