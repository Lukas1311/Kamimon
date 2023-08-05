package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.utils.MeshUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static de.uniks.stpmon.k.constants.TileConstants.TILE_SIZE;

@Singleton
public class PropView extends OpaqueView<Group> {

    public static final int SHADOW_BACK_OFFSET = 8;
    @Inject
    protected WorldRepository repository;
    private Group props;

    @Inject
    public PropView() {
    }

    @Override
    public Node render() {
        SingleCache<List<TileProp>> propCache = repository.props();
        if (propCache.isEmpty()) {
            return new Group();
        }
        props = new Group();
        props.setId("props");
        Group shadow = new Group();
        for (TileProp prop : propCache.asNullable()) {
            Node propNode = createRectangle(ImageUtils.blackOutImage(prop.image(), SHADOW_OPACITY), -90);
            propNode.getTransforms().addAll(getTransforms(prop.height() * TILE_SIZE));
            propNode.setTranslateX(prop.x() * TILE_SIZE);
            propNode.setTranslateZ(-((prop.y() + prop.height()) * TILE_SIZE) + SHADOW_BACK_OFFSET);
            propNode.setTranslateY(-0.2 - 1.5 * Math.random());
            shadow.getChildren().add(propNode);
        }
        shadowNode = shadow;
        props.getChildren().add(shadow);
        for (TileProp prop : propCache.asNullable()) {
            Node propNode = createRectangleScaled(prop.image(), WorldView.WORLD_ANGLE);
            propNode.setTranslateX(prop.x() * TILE_SIZE);
            propNode.setTranslateZ(-((prop.y() + prop.height()) * TILE_SIZE));
            props.getChildren().add(propNode);
        }
        return props;
    }

    @Override
    protected void updateOpacity(Group node, float opacity) {
        if (node == null) {
            return;
        }
        for (Node child : node.getChildren()) {
            if (!(child instanceof MeshView mesh)) {
                continue;
            }
            updateMaterialOpacity(mesh, opacity);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (props != null) {
            for (Node node : props.getChildren()) {
                if (node instanceof Group) {
                    for (Node child : ((Group) node).getChildren()) {
                        MeshUtils.disposeMesh(child);
                    }
                }
                MeshUtils.disposeMesh(node);
            }
            props.getChildren().clear();
            props = null;
        }
    }
}
