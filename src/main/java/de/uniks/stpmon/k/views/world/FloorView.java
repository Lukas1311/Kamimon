package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.map.WorldSet;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

@Singleton
public class FloorView extends WorldViewable {

    @Inject
    protected WorldStorage storage;

    @Inject
    public FloorView() {
    }

    @Override
    public Node render() {
        WorldSet world = storage.getWorld();
        if (world == null) {
            return new Group();
        }
        BufferedImage mapImage = world.groundImage();
        MeshView floor = createPlaneScaled(mapImage);
        floor.setId("floor");
        Bounds bounds = floor.getBoundsInLocal();
        floor.setTranslateX(bounds.getWidth() / 2);
        floor.setTranslateZ(-bounds.getDepth() / 2);

        return floor;
    }
}
