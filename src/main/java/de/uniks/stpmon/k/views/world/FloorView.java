package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.service.TileMapService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

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
        MeshView floor = createPlane(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("map/natchester.png"))));
        floor.setId("floor");

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
