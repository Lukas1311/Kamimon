package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.utils.TileProp;
import de.uniks.stpmon.k.utils.World;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PropView extends WorldController {

    @Inject
    protected WorldStorage storage;

    @Inject
    public PropView() {
    }

    @Override
    public Node render(int angle, PerspectiveCamera camera) {
        World world = storage.getWorld();
        Group props = new Group();
        props.setId("props");
        for (TileProp prop : world.props()) {
            Node propNode = createRectangleScaled(prop.image(), angle);
            propNode.setTranslateX(prop.x() * 16);
            propNode.setTranslateZ(-((prop.y() + prop.height()) * 16));
            props.getChildren().add(propNode);
        }
        return props;
    }
}
