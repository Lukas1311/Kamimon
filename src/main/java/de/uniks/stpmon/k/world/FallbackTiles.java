package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.utils.Direction;

import java.util.*;

import static de.uniks.stpmon.k.constants.TileConstants.TILE_SIZE;

public class FallbackTiles {
    private static final HashMap<Integer, IReplacement> replacements = new HashMap<>();
    private static final Random rand = new Random();

    static {
        RandomTile grass = new RandomTile(405, 405, 406);
        replacements.put(405, grass);
        replacements.put(406, grass);
        RandomTile grassWater = new RandomTile(483, 483, 484);
        replacements.put(483, grassWater);
        replacements.put(484, grassWater);
        DirectionalTile waterEdgeTop = new DirectionalTile(308, 483, 483, 483, 483);
        replacements.put(307, waterEdgeTop);
        replacements.put(308, waterEdgeTop);
        replacements.put(839, waterEdgeTop);
        replacements.put(840, waterEdgeTop);
        replacements.put(841, waterEdgeTop);
        DirectionalTile waterEdgeCorner = new DirectionalTile(306, 482, 482, 482, 482);
        replacements.put(306, waterEdgeCorner);
    }

    private final ChunkBuffer buffer;
    private final TileLayerData layerData;
    private final Map<Integer, Integer> fallbacks = new HashMap<>();

    public FallbackTiles(ChunkBuffer buffer, TileLayerData layerData) {
        this.buffer = buffer;
        this.layerData = layerData;
        rand.setSeed(layerData.name() != null ? layerData.name().hashCode() : 0);
    }

    public int getTile(int x, int y) {
        if (x >= layerData.width()) {
            return getTile(layerData.width() - 1, y);
        }
        if (x < 0) {
            return getTile(0, y);
        }
        if (y >= layerData.height()) {
            return getTile(x, layerData.height() - 1);
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
        Queue<Integer> queue = new PriorityQueue<>(this::compareCandidates);
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
            IReplacement replacement = replacements.get(value);
            if (replacement != null) {
                int diffX = tileX - x;
                int diffY = tileY - y;
                Direction direction;
                if (diffX > 0) {
                    direction = Direction.RIGHT;
                } else if (diffX < 0) {
                    direction = Direction.LEFT;
                } else if (diffY > 0) {
                    direction = Direction.BOTTOM;
                } else {
                    direction = Direction.TOP;
                }
                return replacement.getOverride(direction, rand);
            }
            return value;
        }
        return 0;
    }

    private int compareCandidates(int a, int b) {
        int aX = a % layerData.width();
        int aY = a / layerData.width();
        int bX = b % layerData.width();
        int bY = b / layerData.width();
        int edge = liesOnEdge(aX, aY, layerData.width(), layerData.height());
        return switch (edge) {
            case 0 -> bY - aY;
            case 1 -> aX - bX;
            case 2 -> aY - bY;
            case 3 -> bX - aX;
            default -> 0;
        };
    }

    /**
     * Checks in which section of a rectangle that is devided by two diagonals a point lies.
     *
     * @param x x coordinate of the point
     * @param y y coordinate of the point
     * @param w width of the rectangle
     * @param h height of the rectangle
     * @return 0 = top edge, 1 = right edge, 2 = bottom edge, 3 = left edge, 4 = diagonal, 5 = outside
     */
    public static int liesOnEdge(double x, double y, double w, double h) {
        if (y < 0 || y >= h || x < 0 || x >= w) {
            return 5;  //outside result if needed
        }

        double secondDiagonal = y * w + x * h - h * w;
        if (y * w - x * h == 0 || secondDiagonal == 0) {
            return 4;  //lies on diagonal
        }
        //note possible issues due to float precision limitations
        //better to compare fabs() with small epsylon value

        int code = 0;
        if (secondDiagonal > 0) {
            code += 1;  //above second diagonal
        }

        if (y * w - x * h > 0) {
            code += 2;    //above main diagonal
            code = 5 - code;    //flip 2/3 values to get your numbering
        }
        return code;
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

    private interface IReplacement {

        int getOverride(Direction dir, Random random);
    }

    private record RandomTile(int id, int... overrides) implements IReplacement {

        @Override
        public int getOverride(Direction dir, Random random) {
            return overrides[random.nextInt(overrides.length)];
        }
    }

    private record DirectionalTile(int id, int... overrides) implements IReplacement {

        @Override
        public int getOverride(Direction dir, Random random) {
            return overrides[dir.ordinal()];
        }
    }
}
