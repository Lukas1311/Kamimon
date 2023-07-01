package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;
import javafx.scene.Group;
import javafx.scene.Node;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class PropView extends WorldViewable {

    @Inject
    protected WorldRepository repository;

    @Inject
    public PropView() {
    }

    @Override
    public Node render() {
        SingleCache<List<TileProp>> propCache = repository.props();
        if (propCache.isEmpty()) {
            return new Group();
        }
        Group props = new Group();
        props.setId("props");
        for (TileProp prop : propCache.asNullable()) {
            Node propNode = createRectangleScaled(prop.image(), WorldView.WORLD_ANGLE);
            propNode.setTranslateX(prop.x() * 16);
            propNode.setTranslateZ(-((prop.y() + prop.height()) * 16));
            props.getChildren().add(propNode);
        }
        return props;
    }

}
