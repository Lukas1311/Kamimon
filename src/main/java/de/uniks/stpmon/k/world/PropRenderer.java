package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.utils.ImageUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

import static de.uniks.stpmon.k.constants.TileConstants.TILE_SIZE;

public class PropRenderer {

    public static final float SCALE_HEIGHT_FACTOR = 0.5f;
    public static final float TILE_MAX_SCALE_FACTOR = 0.6f;
    private final PropInspector inspector;

    public PropRenderer(PropInspector inspector) {
        this.inspector = inspector;
    }

    /**
     * Converts the current unique tile groups into a list of prototypes. Each prototype is a list of tiles
     * which are connected to each other and can be used to create a prop.
     *
     * @return The list of all prototypes
     */
    private Map<Integer, List<PropPrototype>> createGroupedPrototypes() {
        int width = inspector.getWidth();
        Map<Integer, List<PropPrototype>> prototypes = new HashMap<>();
        for (HashSet<Integer> group : inspector.uniqueGroups()) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            //Find min and max x and y values of the group
            for (int i : group) {
                int positionPart = i % inspector.getLayerOffset();
                int x = positionPart % width;
                int y = positionPart / width;
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
            prototypes.computeIfAbsent(maxY, k -> new ArrayList<>()).add(new PropPrototype(minX, minY,
                    maxX - minX + 1, maxY - minY + 1, group));
        }
        return prototypes;
    }

    /**
     * Combines the prototypes into bigger prototypes if they are connected to each other.
     *
     * @param groupedPrototypes The prototypes to combine
     */
    private List<PropPrototype> combinePrototypes(Map<Integer, List<PropPrototype>> groupedPrototypes) {
        List<PropPrototype> prototypes = new ArrayList<>();
        for (Map.Entry<Integer, List<PropPrototype>> entry : groupedPrototypes.entrySet()) {
            prototypes.addAll(mergeIntersecting(entry.getValue()));
        }
        return prototypes;
    }

    /**
     * Merges the given prototypes if they intersect with each other.
     *
     * @param prototypes The prototypes to merge
     * @return The merged prototypes
     */
    private List<PropPrototype> mergeIntersecting(List<PropPrototype> prototypes) {
        List<PropPrototype> mergedPrototypes = new ArrayList<>();

        for (PropPrototype pro : prototypes) {
            boolean merged = false;

            // Check if the current prototype intersects with any previously merged prototype
            for (int i = 0; i < mergedPrototypes.size(); i++) {
                PropPrototype mergedRect = mergedPrototypes.get(i);
                if (pro.intersect(mergedRect, 1)) {
                    // Merge the intersecting prototypes and update the merged prototype in the list
                    mergedPrototypes.set(i, pro.merge(mergedRect));
                    merged = true;
                    break;
                }
            }

            // If the prototype did not intersect with any previously merged prototype, add it as a new merged prototype
            if (!merged) {
                mergedPrototypes.add(pro);
            }
        }

        return mergedPrototypes;
    }

    private float getScaleFactor(PropPrototype prototype, int y) {
        if (prototype.height() <= 4) {
            return 1;
        }
        int distanceToBottom = prototype.height() - y;
        float distance = Math.min(y, distanceToBottom - 4) / ((prototype.height() - 4) * SCALE_HEIGHT_FACTOR);
        distance = Math.max(0, Math.min(1, distance));
        return distanceToBottom <= 4 ? 1 : 1 - distance * TILE_MAX_SCALE_FACTOR;
    }

    private int getYOffset(PropPrototype prototype, int y) {
        if (prototype.height() <= 4) {
            return 0;
        }
        int offset = 0;
        for (int i = 0; i < y; i++) {
            offset += Math.round((1 - getScaleFactor(prototype, i)) * TILE_SIZE);
        }
        return offset;
    }

    /**
     * Extracts the props from the image and returns them in a prop map.
     * Each prop has its own image, position and size.
     * The decorations image is modified to remove the props and also contained in the map.
     *
     * @param decorationLayers Images of all decorations
     * @return A map that contains the props and the modified decorations image.
     */
    PropMap createProps(List<DecorationLayer> decorationLayers) {
        int width = inspector.getWidth();
        // Create graphics for each layer, they are used to get the tiles from the image and remove them later
        List<Graphics2D> graphicsList = new ArrayList<>();
        List<BufferedImage> imagesList = new ArrayList<>();
        for (DecorationLayer layer : decorationLayers) {
            BufferedImage image = layer.image();
            BufferedImage floorDecorations = ImageUtils.createImage(image.getWidth(), image.getHeight());
            Graphics2D graphics = floorDecorations.createGraphics();
            graphics.setBackground(new Color(0, 0, 0, 0));
            graphics.drawImage(image, 0, 0, null);
            graphicsList.add(graphics);
            imagesList.add(floorDecorations);
        }
        Map<Integer, List<PropPrototype>> groupedPrototypes = createGroupedPrototypes();
        List<PropPrototype> prototypes = combinePrototypes(groupedPrototypes);
        List<TileProp> props = new ArrayList<>();
        for (PropPrototype prototype : prototypes) {
            Set<Integer> group = prototype.tiles();
            // Calculate bounds of the prop
            int propWidth = prototype.width();
            int propHeight = prototype.height();
            int sortLayer = Integer.MIN_VALUE;
            int maxOffset = getYOffset(prototype, propHeight);
            BufferedImage img = ImageUtils.createTileImage(propWidth, propHeight);
            Graphics2D propGraphics = img.createGraphics();
            for (int i : group.stream().sorted(Comparator.comparingInt(a -> a / inspector.getLayerOffset())).toList()) {
                int layer = i / inspector.getLayerOffset();
                int positionPart = i % inspector.getLayerOffset();
                Graphics2D layerGraphics = graphicsList.get(layer);
                // Coordinates of the tile in the decoration image
                int absoluteX = positionPart % width;
                int absoluteY = positionPart / width;
                // Coordinates of the tile relative to the prop start
                int relativeX = absoluteX - prototype.x();
                int relativeY = absoluteY - prototype.y();
                BufferedImage source = decorationLayers.get(layer).image();
                AffineTransform at = new AffineTransform();
                at.scale(1.0f, getScaleFactor(prototype, relativeY));
                AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                // Copy pixels from the decoration image to the prop image
                propGraphics.drawImage(source.getSubimage(absoluteX * TILE_SIZE, absoluteY * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                        scaleOp,
                        relativeX * TILE_SIZE, relativeY * TILE_SIZE - getYOffset(prototype, relativeY) + maxOffset);
                // Remove pixels from the decoration image
                layerGraphics.clearRect(absoluteX * TILE_SIZE, absoluteY * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE);
                // Get the highest layer of the prop
                sortLayer = Math.max(sortLayer, layer);
            }
            propGraphics.dispose();

            props.add(new TileProp(img, prototype.x(), prototype.y(), propWidth, propHeight, sortLayer));
        }
        // Sort props by layer, bottom layer first
        props.sort(Comparator.comparingInt(TileProp::sortLayer));
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
}
