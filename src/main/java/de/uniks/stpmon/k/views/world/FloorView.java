package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.service.TileMapService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FloorView extends WorldController {

    @Inject
    protected TileMapService tileMapService;
    @Inject
    protected RegionStorage regionStorage;

    @Inject
    public FloorView() {
    }

    @Override
    public Node render(int angle, PerspectiveCamera camera) {
        MeshView floor = createPlaneScaled("map/natchester.png");
        floor.setId("floor");
        Bounds bounds = floor.getBoundsInLocal();
        floor.setTranslateX(bounds.getWidth() / 2);
        floor.setTranslateZ(-bounds.getDepth() / 2);

        return floor;
    }

    @Override
    public void init() {
        if (regionStorage.isEmpty()) {
            return;
        }
        Area area = regionStorage.getArea();
        if (area == null || area.map() == null) {
            return;
        }
        tileMapService.createMap(area);
    }
}
