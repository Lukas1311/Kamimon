package de.uniks.stpmon.k.world;

import java.util.HashSet;

public record PropPrototype(int x, int y, int width, int height, HashSet<Integer> tiles) {

    public boolean intersect(PropPrototype other, int marginHorizontal) {
        return x - marginHorizontal <= other.x + other.width
                && x + width + marginHorizontal >= other.x
                && y <= other.y + other.height + marginHorizontal
                && y + height + marginHorizontal >= other.y;
    }

    public PropPrototype merge(PropPrototype other) {
        HashSet<Integer> newTiles = new HashSet<>(tiles);
        newTiles.addAll(other.tiles);
        int newX1 = Math.min(x, other.x);
        int newY1 = Math.min(y, other.y);
        int newX2 = Math.max(x + width, other.x + other.width);
        int newY2 = Math.max(y + height, other.y + other.height);
        return new PropPrototype(newX1, newY1, newX2 - newX1, newY2 - newY1, newTiles);
    }
}
