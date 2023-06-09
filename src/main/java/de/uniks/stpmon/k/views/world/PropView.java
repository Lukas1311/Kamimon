package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.world.WorldSet;
import javafx.scene.Group;
import javafx.scene.Node;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PropView extends WorldViewable {

    @Inject
    protected WorldStorage storage;

    @Inject
    public PropView() {
    }

    @Override
    public Node render() {
        WorldSet world = storage.getWorld();
        Group props = new Group();
        props.setId("props");
        for (TileProp prop : world.props()) {
            Node propNode = createRectangleScaled(prop.image(), WorldView.WORLD_ANGLE);
            propNode.setTranslateX(prop.x() * 16);
            propNode.setTranslateZ(-((prop.y() + prop.height()) * 16));
            props.getChildren().add(propNode);
        }
        return props;
    }
}
