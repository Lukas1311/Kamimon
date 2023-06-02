package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.utils.Direction;
import de.uniks.stpmon.k.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PropInspector {
    public static final int RGB_THRESHOLD = 45;
    public static final int CONNECT_THRESHOLD = 5;
    public static final int CHECKED_PIXELS = 3;
    public static final int TILE_SIZE = 16;
    private final PropGrid grid;
    private int groupId = 1;
    private final Map<Integer, HashSet<Integer>> groups;

    public PropInspector(int width, int height) {
        grid = new PropGrid(width, height);
        groups = new HashMap<>();
    }

    public Set<HashSet<Integer>> uniqueGroups() {
        return new HashSet<>(groups.values());
    }

    public List<TileProp> work(BufferedImage image) {
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                for (Direction dir : new Direction[]{Direction.RIGHT, Direction.BOTTOM}) {
                    int otherX = x + dir.tileX();
                    int otherY = y + dir.tileY();
                    Direction otherDir = dir.opposite();
                    // Check bounds
                    if (otherX < 0 || otherX >= grid.getWidth()
                            || otherY < 0 || otherY >= grid.getHeight()) {
                        continue;
                    }
                    // Check visited
                    if (grid.hasVisited(x, y, dir)
                            || grid.hasVisited(otherX, otherY, otherDir)) {
                        continue;
                    }
                    // Check if the tiles are connected
                    if (checkConnection(x, y, image, dir, otherDir)) {
                        tryMergeGroups(x, y, otherX, otherY);
                    }
                    grid.setVisited(x, y, dir);
                    grid.setVisited(otherX, otherY, otherDir);
                }
            }
        }
        return createProps(image);
    }

    private List<TileProp> createProps(BufferedImage image) {
        List<TileProp> props = new ArrayList<>();
        for (HashSet<Integer> group : uniqueGroups()) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (int i : group) {
                int x = i % grid.getWidth();
                int y = i / grid.getWidth();
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }

            int width = maxX - minX + 1;
            int height = maxY - minY + 1;
            BufferedImage img = new BufferedImage(width * TILE_SIZE, height * TILE_SIZE,
                    BufferedImage.TYPE_4BYTE_ABGR);
            for (int i : group) {
                int x = i % grid.getWidth();
                int y = i / grid.getWidth();
                ImageUtils.copyData(img.getRaster(), image,
                        (x - minX) * TILE_SIZE, (y - minY) * TILE_SIZE,
                        x * TILE_SIZE, y * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE);
            }

            props.add(new TileProp(img, minX, minY, width, height));
        }
        return props;
    }

    public void tryMergeGroups(int x, int y, int otherX, int otherY) {
        int firstGroup = grid.getGroup(x, y);
        int secondGroup = grid.getGroup(otherX, otherY);
        HashSet<Integer> first = groups.get(firstGroup);
        HashSet<Integer> second = groups.get(secondGroup);
        if (firstGroup == 0 && secondGroup == 0) {
            first = new HashSet<>();
            first.add(x + y * grid.getWidth());
            first.add(otherX + otherY * grid.getWidth());
            int groupIndex = groupId++;
            groups.put(groupIndex, first);
            grid.setGroup(x, y, groupIndex);
            grid.setGroup(otherX, otherY, groupIndex);
            return;
        }
        if (first != null && second == null) {
            first.add(otherX + otherY * grid.getWidth());
            grid.setGroup(otherX, otherY, firstGroup);
            return;
        }
        if (first == null && second != null) {
            second.add(x + y * grid.getWidth());
            grid.setGroup(x, y, secondGroup);
            return;
        }
        first = createIfAbsent(x, y, first);
        second = createIfAbsent(otherX, otherY, second);
        firstGroup = grid.getGroup(x, y);
        secondGroup = grid.getGroup(otherX, otherY);
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

    private HashSet<Integer> createIfAbsent(int x, int y, HashSet<Integer> tiles) {
        if (tiles == null) {
            tiles = new HashSet<>();
            tiles.add(x + y * grid.getWidth());
            int groupIndex = groupId++;
            groups.put(groupIndex, tiles);
            grid.setGroup(x, y, groupIndex);
        }
        return tiles;
    }

    public boolean checkConnection(int tileX, int tileY, BufferedImage image, Direction dir, Direction otherDir) {
        int meetThresholds = 0;
        int emptyCount = 0;
        // Iterate over each pixel in the images and compare the edges
        for (int i = 0; i < TILE_SIZE; i++) {
            for (int dist = 0; dist < CHECKED_PIXELS; dist++) {
                int firstX = tileX * TILE_SIZE + dir.imageX(i, dist);
                int firstY = tileY * TILE_SIZE + dir.imageY(i, dist);
                int secondX = (tileX + dir.tileX()) * TILE_SIZE + otherDir.imageX(i, dist);
                int secondY = (tileY + dir.tileY()) * TILE_SIZE + otherDir.imageY(i, dist);
                int first = image.getRGB(firstX, firstY);
                int second = image.getRGB(secondX, secondY);
                int firstAlpha = (first >> 24 & 0xFF);
                int secondAlpha = (second >> 24 & 0xFF);
                // Skip if any pixel is transparent
                if (first == 0 || second == 0 || firstAlpha == 0 || secondAlpha == 0) {
                    emptyCount++;
                    continue;
                }

                // Calculate the grayscale intensity of each pixel
                double intensity1 = (firstAlpha +
                        (first >> 16 & 0xFF) +
                        (first >> 8 & 0xFF) +
                        ((first) & 0xFF)) / 4.0;
                double intensity2 = (secondAlpha +
                        (second >> 16 & 0xFF) +
                        (second >> 8 & 0xFF) +
                        ((second) & 0xFF)) / 4.0;
                // Compare the intensities and check if the difference is above the threshold
                if (Math.abs(intensity1 - intensity2) <= RGB_THRESHOLD) {
                    meetThresholds += 1;
                }
            }
        }
        meetThresholds /= CHECKED_PIXELS;
        emptyCount /= CHECKED_PIXELS;
        // If more than 100% of the pixels are transparent, return false
        if (emptyCount >= TILE_SIZE) {
            return false;
        }

        return meetThresholds >= CONNECT_THRESHOLD;
    }
}