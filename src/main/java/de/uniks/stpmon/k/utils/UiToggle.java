package de.uniks.stpmon.k.utils;

public class UiToggle {

    private boolean value;
    private final boolean initialValue;


    public UiToggle(boolean initialValue) {
        this.initialValue = initialValue;
        reset();
    }

    public boolean toggle() {
        value = !value;
        return value;
    }

    public void reset() {
        value = initialValue;
    }

}
