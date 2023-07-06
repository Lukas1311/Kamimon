package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.utils.Direction;

public class PropGrid {

    private final int[][] grid;
    private final int width;
    private final int height;

    public PropGrid(int width, int height) {
        grid = new int[width][height];
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasVisited(int x, int y, Direction direction) {
        int data = grid[x][y];
        return (data & (1 << direction.ordinal())) > 0;
    }

    public void setVisited(int x, int y, Direction direction) {
        int data = grid[x][y];
        grid[x][y] = data | (1 << direction.ordinal());
    }

    public int getGroup(int x, int y) {
        return grid[x][y] >> 5;
    }

    public void setGroup(int x, int y, int group) {
        grid[x][y] = (grid[x][y] & 0xF) | (group << 5);
    }

}
