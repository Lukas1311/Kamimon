package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.utils.Direction;
import de.uniks.stpmon.k.world.rules.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class PropInspector {

    public static final int TILE_SIZE = 16;
    private final PropGrid[] grids;
    private final int layerOffset;
    private int groupId = 1;
    private final Map<Integer, HashSet<Integer>> groups;
    private final List<PropRule> connectionRules = new ArrayList<>();
    private final List<PropRule> tileRules = new ArrayList<>();
    private final List<CandidateRule> candidateRules = new ArrayList<>();

    public PropInspector(int width, int height, int layers) {
        grids = new PropGrid[layers];
        for (int i = 0; i < layers; i++) {
            grids[i] = new PropGrid(width, height);
        }
        layerOffset = width * height;
        groups = new HashMap<>();
        // City Fence extraction
        addConnectionRule(new ExclusionRule("../../tilesets/Modern_Exteriors_16x16.json",
                2141, 2143, 3194, 3193));
        // modular fence extraction
        addConnectionRule(new ExclusionRule("../../tilesets/Modern_Exteriors_16x16.json",
                32999, 33002, 33175, 33178));
        // forest fence extraction
        addConnectionRule(new EntangledRule("../../tilesets/Modern_Exteriors_16x16.json",
                new IdSource.Rectangle(1336, 3, 1, 176)));
        // start house porch
        addConnectionRule(new ExclusionRule("../../tilesets/Modern_Exteriors_16x16.json",
                new IdSource.Rectangle(10118, 8, 2, 176),
                new IdSource.Rectangle(9422, 1, 6, 176)));
        // City Fence entangled
        addConnectionRule(new EntangledRule("../../tilesets/Modern_Exteriors_16x16.json",
                new IdSource.Rectangle(2137, 8, 8, 176),
                new IdSource.Rectangle(1789, 3, 2, 176),
                new IdSource.Rectangle(383, 2, 9, 176)));
        // Modular fence entangled
        addConnectionRule(new EntangledRule("../../tilesets/Modern_Exteriors_16x16.json",
                new IdSource.Rectangle(32294, 37, 7, 176)));
        // Lantern entangled
        addConnectionRule(new EntangledRule("../../tilesets/Modern_Exteriors_16x16.json",
                new IdSource.Rectangle(3001, 1, 4, 176)));
        // house carpets
        addConnectionRule(new ExclusionRule("../../tilesets/Modern_Interiors_16x16.json",
                new IdSource.Rectangle(8285 + 247, 4, 3, 16),
                new IdSource.Rectangle(8285 + 173, 4, 14, 16),
                new IdSource.Rectangle(8285 + 251, 2, 6, 16)));
        // house counter
        addConnectionRule(new ExclusionRule("../../tilesets/Modern_Interiors_16x16.json",
                new IdSource.Rectangle(8285 + 10, 5, 5, 16),
                new IdSource.Single(8285 + 407),
                new IdSource.Single(8285 + 60)));
        // picnic
        addConnectionRule(new ExclusionRule("../../tilesets/Modern_Exteriors_16x16.json",
                new IdSource.Rectangle(1135, 5, 8, 176)));
        addConnectionRule(new ImageConnectionRule());
        addTileRule(new ImageEmptyRule());
        addCandidateRule(new TilesetCandidateRule("../../tilesets/Modern_Exteriors_16x16.json", 176));
        addCandidateRule(new IncludedCandidateRule("../../tilesets/Modern_Exteriors_16x16.json", 12141, 13021));
        addCandidateRule(new IncludedCandidateRule("../../tilesets/Modern_Exteriors_16x16.json", 10553, 10377));
    }

    @SuppressWarnings("UnusedReturnValue")
    public PropInspector addTileRule(PropRule rule) {
        tileRules.add(rule);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public PropInspector addConnectionRule(PropRule rule) {
        connectionRules.add(rule);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public PropInspector addCandidateRule(CandidateRule rule) {
        candidateRules.add(rule);
        return this;
    }

    public Set<HashSet<Integer>> uniqueGroups() {
        return new HashSet<>(groups.values());
    }


    private RuleResult applyRules(Collection<PropRule> rules, PropInfo info, List<DecorationLayer> decorationLayers) {
        for (PropRule rule : rules) {
            RuleResult result = rule.apply(info, decorationLayers);
            if (result != RuleResult.NO_MATCH) {
                return result;
            }
        }
        return RuleResult.NO_MATCH;
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
                    for (Direction dir : new Direction[]{Direction.RIGHT, Direction.TOP}) {
                        int otherX = x + dir.tileX();
                        int otherY = y + dir.tileY();
                        Direction otherDir = dir.opposite();
                        // Check bounds
                        if (otherX < 0 || otherX >= grid.getWidth()
                                || otherY < 0 || otherY >= grid.getHeight()) {
                            continue;
                        }
                        List<PropInfo> candidates = new ArrayList<>();
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
                            PropInfo info = new PropInfo(x, y, layerIndex, id, otherId, otherLayer,
                                    data.getTileset(id).source(), data.getTileset(otherId).source(), dir, otherDir);
                            RuleResult result = applyRules(connectionRules,
                                    info, decorationLayers
                            );
                            if (result == RuleResult.NO_MATCH_DECORATION) {
                                marked = true;
                                continue;
                            }
                            if (result == RuleResult.NO_MATCH_STOP) {
                                continue;
                            }
                            // Check if the tiles are connected
                            if (result == RuleResult.MATCH_CONNECTION) {
                                candidates.add(info);
                            }
                        }
                        if (candidates.isEmpty()) {
                            continue;
                        }
                        PropInfo bestCandidate = null;
                        if (candidates.size() > 1) {
                            for (CandidateRule rule : candidateRules) {
                                bestCandidate = rule.apply(candidates, decorationLayers);
                                if (bestCandidate != null) {
                                    break;
                                }
                            }
                        } else {
                            bestCandidate = candidates.get(0);
                        }
                        if (bestCandidate != null) {
                            int otherLayer = bestCandidate.otherLayer();
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
                    for (Direction dir : new Direction[]{Direction.LEFT, Direction.BOTTOM}) {
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
                            marked = true;
                            break;
                        }
                    }
                    if (marked) {
                        continue;
                    }
                    RuleResult result = applyRules(tileRules,
                            new PropInfo(x, y, layerIndex, id, -1, -1,
                                    data.getTileset(id).source(), null, null, null), decorationLayers
                    );
                    if (result == RuleResult.MATCH_SINGLE) {
                        createIfAbsent(grid, x, y, null, layerIndex);
                    }
                }
            }
        }
        return createProps(decorationLayers);
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
            for (int i : group.stream().sorted(Comparator.<Integer>comparingInt(a -> a / layerOffset).reversed()).toList()) {
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
