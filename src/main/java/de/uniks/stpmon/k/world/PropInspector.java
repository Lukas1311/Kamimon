package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.utils.Direction;
import de.uniks.stpmon.k.world.rules.RuleRegistry;
import de.uniks.stpmon.k.world.rules.RuleResult;
import de.uniks.stpmon.k.world.rules.TileInfo;

import java.util.*;

/**
 * The PropInspector is responsible for creating the props from the given tile map data and the decoration layers.
 * These layers are used to determine the props and their positions. These textures are then used to create the props.
 * <p/>
 * The container {@link RuleRegistry} defines the way the props are defined.
 */
public class PropInspector {
    public static final Direction[] WALK_DIRECTIONS = {Direction.RIGHT, Direction.TOP};
    public static final Direction[] WALK_DIRECTIONS_OPPOSITES = {Direction.LEFT, Direction.BOTTOM};
    private final PropGrid[] grids;
    /**
     * The offset to the tile id that a different layer has.
     */
    private final int layerOffset;
    private final RuleRegistry registry;
    /**
     * The current group id. This is used to group the tile together as one prop.
     */
    private int groupId = 1;
    /**
     * The groups of tiles. Each group contains the tile ids of the tiles that belong to the group. Every group is later
     * used to create a single prop.
     */
    private final Map<Integer, HashSet<Integer>> groups;

    public PropInspector(int width, int height, int layers, RuleRegistry registry) {
        grids = new PropGrid[layers];
        for (int i = 0; i < layers; i++) {
            grids[i] = new PropGrid(width, height);
        }
        layerOffset = width * height;
        groups = new HashMap<>();
        this.registry = registry;
    }

    public Set<HashSet<Integer>> uniqueGroups() {
        return new HashSet<>(groups.values());
    }

    public PropMap work(DecorationLayer layer, TileMapData data) {
        return work(List.of(layer), data);
    }

    public PropMap work(List<DecorationLayer> decorationLayers, TileMapData data) {
        ChunkBuffer[] buffers = new ChunkBuffer[decorationLayers.size()];
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = new ChunkBuffer(decorationLayers.get(i).layerData());
        }
        for (int layerIndex = 0; layerIndex < decorationLayers.size(); layerIndex++) {
            PropContext context = new PropContext(layerIndex, data, buffers, decorationLayers);
            workLayer(context);
        }
        PropRenderer renderer = new PropRenderer(this);
        return renderer.createProps(decorationLayers);
    }

    /**
     * Creates the props from the given context and marks them in the context grid.
     *
     * @param context The context to create the props
     */
    private void workLayer(PropContext context) {
        ChunkBuffer buffer = context.buffers[context.layerIndex];
        PropGrid grid = grids[context.layerIndex];
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = grid.getHeight() - 1; y >= 0; y--) {
                int id = buffer.getId(x, y);
                // Empty or invalid tile
                if (id <= 0) {
                    continue;
                }

                TileInfo current = new TileInfo(x, y, context.layerIndex, id, context.getTileset(id));
                // Check if tile is a decoration or already used
                if (registry.isDecoration(current)) {
                    continue;
                }
                if (registry.isBlending(current)
                        && blendInto(context, x, y)) {
                    continue;
                }

                if (connectTo(context, grid, x, y, current)) {
                    continue;
                }
                if (checkOtherDirections(grid, x, y)) {
                    continue;
                }
                RuleResult result = registry.tryToExtract(current, context.decorationLayers);
                if (result == RuleResult.MATCH_SINGLE) {
                    createIfAbsent(grid, x, y, null, context.layerIndex);
                }
            }
        }
    }

    /**
     * Tries to blend the current tile into the other layers.
     *
     * @param context The current context
     * @param x       The x coordinate
     * @param y       The y coordinate
     */
    private boolean blendInto(PropContext context, int x, int y) {
        for (int otherLayer = 0; otherLayer < context.layerIndex; otherLayer++) {
            PropGrid otherGrid = grids[otherLayer];
            ChunkBuffer otherBuffer = context.buffers[otherLayer];
            // Check visited
            if (notInBounds(otherGrid, x, y)) {
                continue;
            }
            int id = otherBuffer.getId(x, y);
            if (id <= 0) {
                continue;
            }
            tryMergeGroups(x, y, context.layerIndex, x, y, otherLayer);
            return true;
        }
        return false;
    }

    /**
     * Connects the current tile to the best neighbour tiles in the directions right and top.
     * The neighbour tile also has to allow the current tile to connect back. If this is not the case, the neighbour
     * will be ignored.
     *
     * @param context The current context
     * @param grid    The current grid
     * @param x       The x coordinate
     * @param y       The y coordinate
     * @param current The current tile
     * @return True if the current tile was marked as visited
     */
    private boolean connectTo(PropContext context, PropGrid grid, int x, int y, TileInfo current) {
        boolean marked = false;
        for (Direction dir : WALK_DIRECTIONS) {
            ConnectionResult result = findBestNeighbour(context, grid, x, y, current, dir);
            TileInfo bestCandidate = result.connectedTile;
            marked |= result.alreadyVisited;
            if (bestCandidate != null) {
                ConnectionResult back = findBestNeighbour(context, grid,
                        bestCandidate.tileX(), bestCandidate.tileY(),
                        bestCandidate, dir.opposite());
                // Check if the other tile wants to connect back
                if (back.connectedTile == null || back.connectedTile.tileId() != current.tileId()) {
                    continue;
                }
                int otherLayer = bestCandidate.layer();
                PropGrid otherGrid = grids[otherLayer];
                tryMergeGroups(x, y, context.layerIndex, bestCandidate.tileX(), bestCandidate.tileY(), otherLayer);
                marked = true;
                grid.setVisited(x, y, dir);
                otherGrid.setVisited(bestCandidate.tileX(), bestCandidate.tileY(), dir.opposite());
            }
        }
        return marked;
    }

    private int createIndex(int x, int y, int layer) {
        return x + y * grids[0].getWidth() + layer * layerOffset;
    }

    public void tryMergeGroups(int x, int y, int layer, int otherX, int otherY, int otherLayer) {
        PropGrid grid = grids[layer];
        PropGrid otherGrid = grids[otherLayer];
        int firstGroup = grid.getGroup(x, y);
        int secondGroup = otherGrid.getGroup(otherX, otherY);
        HashSet<Integer> first = groups.get(firstGroup);
        HashSet<Integer> second = groups.get(secondGroup);
        if (firstGroup == 0 && secondGroup == 0) {
            first = new HashSet<>();
            first.add(createIndex(x, y, layer));
            first.add(createIndex(otherX, otherY, otherLayer));
            int groupIndex = groupId++;
            groups.put(groupIndex, first);
            grid.setGroup(x, y, groupIndex);
            otherGrid.setGroup(otherX, otherY, groupIndex);
            return;
        }
        if (first != null && second == null) {
            first.add(createIndex(otherX, otherY, otherLayer));
            otherGrid.setGroup(otherX, otherY, firstGroup);
            return;
        }
        if (first == null && second != null) {
            second.add(createIndex(x, y, layer));
            grid.setGroup(x, y, secondGroup);
            return;
        }
        first = createIfAbsent(grid, x, y, first, layer);
        second = createIfAbsent(otherGrid, otherX, otherY, second, otherLayer);
        firstGroup = grid.getGroup(x, y);
        secondGroup = otherGrid.getGroup(otherX, otherY);
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

    /**
     * Checks if the given group is present (not null). If not, a new group is created and the tile is added to it.
     *
     * @param grid  The grid to check
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param tiles The group which can be null
     * @param layer The index of layer
     * @return The group which is guaranteed to be not null
     */
    private HashSet<Integer> createIfAbsent(PropGrid grid, int x, int y, HashSet<Integer> tiles, int layer) {
        if (tiles == null) {
            tiles = new HashSet<>();
            tiles.add(createIndex(x, y, layer));
            int groupIndex = groupId++;
            groups.put(groupIndex, tiles);
            grid.setGroup(x, y, groupIndex);
        }
        return tiles;
    }

    /**
     * Tries to find the best neighbour tile for the given coordinates and direction.
     * The best neighbour tile is a tile which can be connected to the current tile and is chosen by the
     * candidate rules as the best candidate.
     *
     * @param context The current context
     * @param grid    The current grid
     * @param x       The x coordinate
     * @param y       The y coordinate
     * @param current The current tile
     * @param dir     The direction to check
     * @return The best neighbour tile or null if no neighbour was found
     */
    private ConnectionResult findBestNeighbour(PropContext context, PropGrid grid, int x, int y, TileInfo current, Direction dir) {
        int otherX = x + dir.tileX();
        int otherY = y + dir.tileY();
        Direction otherDir = dir.opposite();
        // Check bounds
        if (notInBounds(grid, otherX, otherY)) {
            return ConnectionResult.NO_CONNECTION;
        }
        boolean marked = false;
        List<TileInfo> candidates = new ArrayList<>();
        for (int otherLayer = 0; otherLayer < context.decorationLayers.size(); otherLayer++) {
            PropGrid otherGrid = grids[otherLayer];
            ChunkBuffer otherBuffer = context.buffers[otherLayer];
            // Check visited
            if (grid.hasVisited(x, y, dir)
                    || otherGrid.hasVisited(otherX, otherY, otherDir)) {
                marked = true;
                continue;
            }
            int otherId = otherBuffer.getId(otherX, otherY);
            // Empty or invalid tile
            if (otherId <= 0) {
                continue;
            }

            TileInfo other = new TileInfo(otherX, otherY, otherLayer, otherId, context.getTileset(otherId));
            // Check if tile is a decoration or already used
            if (registry.isDecoration(other)) {
                continue;
            }
            // Apply connection rules
            RuleResult result = registry.tryToConnect(current, other, dir, otherDir, context.decorationLayers);
            if (result == RuleResult.NO_MATCH_STOP) {
                continue;
            }
            // Check if the tiles are connected
            if (result == RuleResult.MATCH_CONNECTION) {
                candidates.add(other);
            }
        }
        if (candidates.isEmpty()) {
            return new ConnectionResult(marked, null);
        }
        TileInfo bestCandidate = registry.getPropInfo(current, candidates, context.decorationLayers);
        return new ConnectionResult(marked, bestCandidate);
    }

    /**
     * Check if the tile at the given coordinates in the given grid was already visited in the directions
     * that are not the main directions (right and top).
     *
     * @param grid The grid to check against
     * @param x    The x coordinate to check
     * @param y    The y coordinate to check
     * @return True if the tile was already visited, false otherwise
     */
    private static boolean checkOtherDirections(PropGrid grid, int x, int y) {
        for (Direction dir : WALK_DIRECTIONS_OPPOSITES) {
            int otherX = x + dir.tileX();
            int otherY = y + dir.tileY();
            Direction otherDir = dir.opposite();
            // Check bounds
            if (notInBounds(grid, otherX, otherY)) {
                continue;
            }
            // Check visited
            if (!grid.hasVisited(x, y, dir)
                    && !grid.hasVisited(otherX, otherY, otherDir)) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the given coordinates are out of bounds.
     *
     * @param grid The grid to check against
     * @param x    The x coordinate to check
     * @param y    The y coordinate to check
     * @return True if the coordinates are out of bounds, false otherwise
     */
    private static boolean notInBounds(PropGrid grid, int x, int y) {
        return x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight();
    }

    /**
     * Width of the tile map in tiles.
     *
     * @return The width in tiles
     */
    public int getWidth() {
        return grids[0].getWidth();
    }

    /**
     * The offset to the tile id that a different layer has.
     *
     * @return The offset to the tile id
     */
    public int getLayerOffset() {
        return layerOffset;
    }

    private record PropContext(int layerIndex, TileMapData data, ChunkBuffer[] buffers,
                               List<DecorationLayer> decorationLayers) {
        public String getTileset(int id) {
            return data.getTileset(id).source();
        }
    }

    private record ConnectionResult(boolean alreadyVisited, TileInfo connectedTile) {
        public static final ConnectionResult NO_CONNECTION = new ConnectionResult(false, null);
    }
}
