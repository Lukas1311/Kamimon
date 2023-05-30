package de.uniks.stpmon.k.views.world;

import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;

import javax.inject.Inject;

public class PropView extends WorldController {

    @Inject
    public PropView() {
    }

    @Override
    public Node render(int angle, PerspectiveCamera camera) {
        Node building = createRectangleScaled("map/building.png", angle);
        building.setTranslateX(1280.0 / 2);
        building.setTranslateZ(-1280.0 / 2);
        return building;
    }
}
