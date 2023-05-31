package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.TileMapService;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.utils.TileMap;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

@Singleton
public class FloorView extends WorldController {

    @Inject
    protected TileMapService tileMapService;
    @Inject
    protected WorldStorage storage;

    @Inject
    public FloorView() {
    }

    @Override
    public Node render(int angle, PerspectiveCamera camera) {
        TileMap tileMap = storage.getTileMap();
        BufferedImage mapImage = tileMap.renderMap();
        MeshView floor = createPlaneScaled(mapImage);
        floor.setId("floor");
        Bounds bounds = floor.getBoundsInLocal();
        floor.setTranslateX(bounds.getWidth() / 2);
        floor.setTranslateZ(-bounds.getDepth() / 2);

        return floor;
    }
}
