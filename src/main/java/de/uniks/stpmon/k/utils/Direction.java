package de.uniks.stpmon.k.utils;

import static de.uniks.stpmon.k.service.world.PropInspector.TILE_SIZE;

public enum Direction {
    RIGHT,
    TOP,
    LEFT,
    BOTTOM;

    public int imageX(int i, int dist) {
        return switch (this) {
            case LEFT -> dist;
            case TOP, BOTTOM -> i;
            case RIGHT -> TILE_SIZE - 1 - dist;
        };
    }

    public int imageY(int i, int dist) {
        return switch (this) {
            case LEFT, RIGHT -> i;
            case TOP -> dist;
            case BOTTOM -> TILE_SIZE - 1 - dist;
        };
    }

    public int tileX() {
        return switch (this) {
            case LEFT -> -1;
            case TOP, BOTTOM -> 0;
            case RIGHT -> 1;
        };
    }

    public int tileY() {
        return switch (this) {
            case LEFT, RIGHT -> 0;
            case TOP -> -1;
            case BOTTOM -> 1;
        };
    }

    public Direction opposite() {
        return switch (this) {
            case LEFT -> RIGHT;
            case TOP -> BOTTOM;
            case RIGHT -> LEFT;
            case BOTTOM -> TOP;
        };
    }
}
