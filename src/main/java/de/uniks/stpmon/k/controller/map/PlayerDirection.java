package de.uniks.stpmon.k.controller.map;

public enum PlayerDirection {
    RIGHT(90), // 0
    UP(0),     // 1
    LEFT(270), // 2
    DOWN(180); // 3

    private final int degrees;

    PlayerDirection(final int degrees) {
        this.degrees = degrees;
    }

    public int getDegrees() {
        return degrees;
    }
}
