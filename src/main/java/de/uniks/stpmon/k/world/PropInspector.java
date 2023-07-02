package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.utils.Direction;
import de.uniks.stpmon.k.world.rules.BasicRules;
import de.uniks.stpmon.k.world.rules.RuleRegistry;
import de.uniks.stpmon.k.world.rules.RuleResult;
import de.uniks.stpmon.k.world.rules.TileInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class PropInspector {
    private static final RuleRegistry registry = BasicRules.registerRules();
    public static final int TILE_SIZE = 16;
    private final PropGrid[] grids;
    private final int layerOffset;
    private int groupId = 1;
    private final Map<Integer, HashSet<Integer>> groups;

    public PropInspector(int width, int height, int layers) {
        grids = new PropGrid[layers];
        for (int i = 0; i < layers; i++) {
            grids[i] = new PropGrid(width, height);
        }
        layerOffset = width * height;
        groups = new HashMap<>();
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
            workLayer(decorationLayers, data, buffers, layerIndex);
        }
        return createProps(decorationLayers);
    }

    private void workLayer(List<DecorationLayer> decorationLayers, TileMapData data, ChunkBuffer[] buffers, int layerIndex) {
        ChunkBuffer buffer = buffers[layerIndex];
        PropGrid grid = grids[layerIndex];
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = grid.getHeight() - 1; y >= 0; y--) {
                boolean marked = false;
                int id = buffer.getId(x, y);
                // Empty or invalid tile
                if (id <= 0) {
                    continue;
                }

                TileInfo current = new TileInfo(x, y, layerIndex, id, data.getTileset(id).source());
                // Check if tile is a decoration or already used
                if (registry.isDecoration(current)) {
                    grid.setUsed(x, y, true);
                    continue;
                }

                for (Direction dir : new Direction[]{Direction.RIGHT, Direction.TOP}) {
                    int otherX = x + dir.tileX();
                    int otherY = y + dir.tileY();
                    Direction otherDir = dir.opposite();
                    // Check bounds
                    if (notInBounds(grid, otherX, otherY)) {
                        continue;
                    }
                    List<TileInfo> candidates = new ArrayList<>();
                    for (int otherLayer = 0; otherLayer < decorationLayers.size(); otherLayer++) {
                        PropGrid otherGrid = grids[otherLayer];
                        ChunkBuffer otherBuffer = buffers[otherLayer];
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

                        TileInfo other = new TileInfo(x, y, otherLayer, otherId, data.getTileset(otherId).source());
                        // Check if tile is a decoration or already used
                        if (registry.isDecoration(other)) {
                            continue;
                        }
                        RuleResult result = registry.applyRule(current, other, dir, otherDir, decorationLayers);
                        if (result == RuleResult.NO_MATCH_STOP) {
                            continue;
                        }
                        // Check if the tiles are connected
                        if (result == RuleResult.MATCH_CONNECTION) {
                            candidates.add(other);
                        }
                    }
                    if (candidates.isEmpty()) {
                        continue;
                    }
                    TileInfo bestCandidate = registry.getPropInfo(current, candidates, decorationLayers);
                    if (bestCandidate != null) {
                        int otherLayer = bestCandidate.layer();
                        PropGrid otherGrid = grids[otherLayer];
                        tryMergeGroups(x, y, layerIndex, otherX, otherY, otherLayer);
                        marked = true;
                        grid.setVisited(x, y, dir);
                        otherGrid.setVisited(otherX, otherY, otherDir);
                    }
                }
                if (marked) {
                    continue;
                }
                if (checkOtherDirections(grid, x, y)) {
                    continue;
                }
                RuleResult result = registry.applyLoneRule(current, decorationLayers);
                if (result == RuleResult.MATCH_SINGLE) {
                    createIfAbsent(grid, x, y, null, layerIndex);
                }
            }
        }
    }

    private static boolean checkOtherDirections(PropGrid grid, int x, int y) {
        for (Direction dir : new Direction[]{Direction.LEFT, Direction.BOTTOM}) {
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

    private static boolean notInBounds(PropGrid grid, int otherX, int otherY) {
        return otherX < 0 || otherX >= grid.getWidth()
                || otherY < 0 || otherY >= grid.getHeight();
    }


    /**
     * Extracts the props from the image and returns them in a prop map.
     * Each prop has its own image, position and size.
     * The decorations image is modified to remove the props and also contained in the map.
     *
     * @param decorationLayers Images of all decorations
     * @return A map that contains the props and the modified decorations image.
     */
    private PropMap createProps(List<DecorationLayer> decorationLayers) {
        int width = grids[0].getWidth();
        List<Graphics2D> graphicsList = new ArrayList<>();
        List<BufferedImage> imagesList = new ArrayList<>();
        for (DecorationLayer layer : decorationLayers) {
            BufferedImage image = layer.image();
            BufferedImage floorDecorations = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = floorDecorations.createGraphics();
            graphics.setBackground(new Color(0, 0, 0, 0));
            graphics.drawImage(image, 0, 0, null);
            graphicsList.add(graphics);
            imagesList.add(floorDecorations);
        }
        List<TileProp> props = new ArrayList<>();
        for (HashSet<Integer> group : uniqueGroups()) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            //Find min and max x and y values of the group
            for (int i : group) {
                int positionPart = i % layerOffset;
                int x = positionPart % width;
                int y = positionPart / width;
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }

            // Calculate bounds of the prop
            int propWidth = maxX - minX + 1;
            int propHeight = maxY - minY + 1;
            BufferedImage img = new BufferedImage(propWidth * TILE_SIZE, propHeight * TILE_SIZE,
                    BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D propGraphics = img.createGraphics();
            for (int i : group.stream().sorted(Comparator.comparingInt(a -> a / layerOffset)).toList()) {
                int layer = i / layerOffset;
                int positionPart = i % layerOffset;
                Graphics2D layerGraphics = graphicsList.get(layer);
                int x = positionPart % width;
                int y = positionPart / width;
                BufferedImage source = decorationLayers.get(layer).image();
                // Copy pixels from the decoration image to the prop image
                propGraphics.drawImage(source.getSubimage(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                        null, (x - minX) * TILE_SIZE, (y - minY) * TILE_SIZE);
                // Remove pixels from the decoration image
                layerGraphics.clearRect(x * TILE_SIZE, y * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE);
            }
            propGraphics.dispose();

            props.add(new TileProp(img, minX, minY, propWidth, propHeight));
        }
        // take first layer to draw all decoration layers bottom up
        Graphics2D baseGraphics = graphicsList.get(0);
        for (int i = 1; i < graphicsList.size(); i++) {
            baseGraphics.drawImage(imagesList.get(i), 0, 0, null);
        }
        for (Graphics2D graphics : graphicsList) {
            graphics.dispose();
        }
        // Return the props and the modified decorations image
        return new PropMap(props, imagesList.get(0));
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
}
