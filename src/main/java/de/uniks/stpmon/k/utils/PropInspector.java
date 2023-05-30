package de.uniks.stpmon.k.utils;

import de.uniks.stpmon.k.models.map.TileMapData;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropInspector {
    public static final int RGB_THRESHOLD = 0;
    public static final int CONNECT_THRESHOLD = 8;
    public static final int TILE_SIZE = 16;
    private final int[][] grid;
    private final Map<Integer, HashSet<Integer>> groups;
    private int groupId = 1;

    public PropInspector(TileMapData data) {
        grid = new int[data.width()][data.height()];
        groups = new HashMap<>();
    }

    private boolean hasVisited(int x, int y, Direction direction) {
        int data = grid[x][y];
        return (data & (1 << direction.ordinal())) > 0;
    }

    public void setVisited(int x, int y, Direction direction) {
        int data = grid[x][y];
        grid[x][y] = data | (1 << direction.ordinal());
    }

    public int getGroup(int x, int y) {
        return grid[x][y] >> 4;
    }

    public void setGroup(int x, int y, int group) {
        grid[x][y] = (grid[x][y] & 0xF) | (group << 4);
    }

    public Set<HashSet<Integer>> uniqueGroups() {
        return new HashSet<>(groups.values());
    }

    public void work(BufferedImage image) {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                for (Direction dir : Direction.values()) {
                    int otherX = x + dir.tileX();
                    int otherY = y + dir.tileY();
                    Direction otherDir = dir.opposite();
                    // Check bounds
                    if (otherX < 0 || otherX >= grid.length
                            || otherY < 0 || otherY >= grid[x].length) {
                        continue;
                    }
                    // Check visited
                    if (hasVisited(x, y, dir)
                            || hasVisited(otherX, otherY, otherDir)) {
                        continue;
                    }
                    // Check if the tiles are connected
                    if (checkConnection(x, y, image, dir, otherDir)) {
                        updateGroup(x, y, otherX, otherY);
                    }
                    setVisited(x, y, dir);
                    setVisited(otherX, otherY, otherDir);
                }
            }
        }
    }

    private void updateGroup(int x, int y, int otherX, int otherY) {
        int firstGroup = getGroup(x, y);
        int secondGroup = getGroup(otherX, otherY);
        HashSet<Integer> first = groups.get(firstGroup);
        HashSet<Integer> second = groups.get(secondGroup);
        if (firstGroup == 0 && secondGroup == 0) {
            first = new HashSet<>();
            first.add(x + y * grid.length);
            first.add(otherX + otherY * grid.length);
            int groupIndex = groupId++;
            groups.put(groupIndex, first);
            setGroup(x, y, groupIndex);
            setGroup(otherX, otherY, groupIndex);
            return;
        }
        first = setGroup(x, y, first);
        second = setGroup(otherX, otherY, second);
        if (firstGroup != secondGroup) {
            if (first.size() >= second.size()) {
                first.addAll(second);
                groups.put(secondGroup, first);
            } else {
                second.addAll(first);
                groups.put(firstGroup, second);
            }
        }
    }

    private HashSet<Integer> setGroup(int x, int y, HashSet<Integer> tiles) {
        if (tiles == null) {
            tiles = new HashSet<>();
            tiles.add(x + y * grid.length);
            int groupIndex = groupId++;
            groups.put(groupIndex, tiles);
            setGroup(x, y, groupIndex);
        }
        return tiles;
    }

    public boolean checkConnection(int tileX, int tileY, BufferedImage image, Direction dir, Direction otherDir) {
        int meetThresholds = 0;

        // Iterate over each pixel in the images and compare the edges
        for (int i = 0; i < TILE_SIZE; i++) {
            int firstX = tileX * TILE_SIZE + dir.imageX(i);
            int firstY = tileY * TILE_SIZE + dir.imageY(i);
            int secondX = (tileX + dir.tileX()) * TILE_SIZE + otherDir.imageX(i);
            int secondY = (tileY + dir.tileY()) * TILE_SIZE + otherDir.imageY(i);
//            int first = image.getRGB(tileX * TILE_SIZE + dir.imageX(i),
//                    tileY * TILE_SIZE + dir.imageY(i));
//            int second = image.getRGB(tileX * TILE_SIZE + otherDir.imageX(i),
//                    tileY * TILE_SIZE + otherDir.imageY(i));
            int first = image.getRGB(firstX, firstY);
            int second = image.getRGB(secondX, secondY);

            // Calculate the grayscale intensity of each pixel
            double intensity1 = (((first >> 24 & 0xFF)) + (first >> 16 & 0xFF) + (first >> 8 & 0xFF) + ((first) & 0xFF)) / 4.0;
            double intensity2 = (((second >> 24 & 0xFF)) + (second >> 16 & 0xFF) + (second >> 8 & 0xFF) + ((second) & 0xFF)) / 4.0;

            // Compare the intensities and check if the difference is above the threshold
            if (Math.abs(intensity1 - intensity2) <= RGB_THRESHOLD) {
                meetThresholds += 1;
            }
        }

        return meetThresholds > CONNECT_THRESHOLD;
    }

    private enum Direction {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM;

        public int imageX(int i) {
            return switch (this) {
                case LEFT -> 0;
                case TOP, BOTTOM -> i;
                case RIGHT -> TILE_SIZE - 1;
            };
        }

        public int imageY(int i) {
            return switch (this) {
                case LEFT, RIGHT -> i;
                case TOP -> 0;
                case BOTTOM -> TILE_SIZE - 1;
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
}
