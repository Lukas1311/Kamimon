package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.util.*;

import static de.uniks.stpmon.k.constants.TileConstants.TILE_SIZE;

public class FallbackTiles {
    private final ChunkBuffer buffer;
    private final TileLayerData layerData;
    private final Map<Integer, Integer> fallbacks = new HashMap<>();

    public FallbackTiles(ChunkBuffer buffer, TileLayerData layerData) {
        this.buffer = buffer;
        this.layerData = layerData;
    }

    public int getTile(int x, int y) {
        if (x > layerData.width()) {
            return getTile(layerData.width(), y);
        }
        if (x < 0) {
            return getTile(0, y);
        }
        if (y > layerData.height()) {
            return getTile(x, layerData.height());
        }
        if (y < 0) {
            return getTile(x, 0);
        }
        boolean walkDown = y > layerData.width() / 2;
        boolean walkRight = x > layerData.width() / 2;
        if (buffer.isInvalid(x, y)) {
            y = y - y % TILE_SIZE;
            x = x - x % TILE_SIZE;
            while (buffer.isInvalid(x, y)) {
                x += TILE_SIZE * (walkRight ? -1 : 1);
                y += TILE_SIZE * (walkDown ? -1 : 1);

                int value = getInternal(x, y);
                if (value <= 0) {
                    continue;
                }
                return value;
            }
        }
        HashSet<Integer> candidates = new HashSet<>();
        HashSet<Integer> visited = new HashSet<>();
        float xDiff = (float) Math.min(x, layerData.width() - x) / layerData.height();
        float yDiff = (float) Math.min(y, layerData.height() - y) / layerData.width();
        boolean sortByX = xDiff >= yDiff;
        Queue<Integer> queue = new PriorityQueue<>((a, b) -> sortByX ? a % layerData.width() - b % layerData.width()
                : a / layerData.width() - b / layerData.width());
        queue.add(x + y * layerData.width());
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            int tileX = poll % layerData.width();
            int tileY = poll / layerData.width();
            int value = getInternal(tileX, tileY);
            visited.add(poll);
            if (value <= 0) {
                addQueue(tileX, tileY + 1, queue, candidates);
                addQueue(tileX, tileY - 1, queue, candidates);
                addQueue(tileX + 1, tileY, queue, candidates);
                addQueue(tileX - 1, tileY, queue, candidates);
                continue;
            }
            // Update visited tiles with the value
            for (Integer i : visited) {
                fallbacks.put(i, value);
            }
            return value;
        }
        return 0;
    }

    private void addQueue(int x, int y, Queue<Integer> queue, HashSet<Integer> candidates) {
        if (x < 0 || y < 0 || x >= layerData.width() || y >= layerData.height()) {
            return;
        }
        if (candidates.contains(x + y * layerData.width())) {
            return;
        }
        candidates.add(x + y * layerData.width());
        queue.add(x + y * layerData.width());
    }

    private int getInternal(int x, int y) {
        Integer value = fallbacks.get(x + y * layerData.width());
        if (value != null) {
            return fallbacks.get(x + y * layerData.width());
        }
        if (buffer.isInvalid(x, y)) {
            return -1;
        }
        int tile = buffer.getId(x, y);
        if (tile > 0) {
            fallbacks.put(x + y * layerData.width(), tile);
            return tile;
        }
        return -1;
    }
}
