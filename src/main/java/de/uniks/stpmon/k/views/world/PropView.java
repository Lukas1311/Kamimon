package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;
import de.uniks.stpmon.k.utils.MeshUtils;
import javafx.scene.Group;
import javafx.scene.Node;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class PropView extends WorldViewable {

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
//        for (TileProp prop : propCache.asNullable()) {
//            Node propNode = createRectangleScaled(ImageUtils.blackOutImage(prop.image(), 0.25f),  -90);
////            propNode.setTranslateX(prop.x() * 16);
////            propNode.setTranslateZ(-((prop.y() + prop.height()) * 16) + 6);
////            propNode.setTranslateY(-0.35);
//            propNode.getTransforms().add(0, new Translate(prop.x() * 16, -0.35, -((prop.y() + prop.height()) * 16)));
//            propNode.getTransforms().add(2, new Shear(1, 0,   0, 0));
//            propNode.getTransforms().add(3, new Translate(0, 1, 0));
//            //propNode.getTransforms().add(new Translate(-prop.width() * 8, -(prop.height() * 16) + 6, 0));
//            //propNode.getTransforms().add(new Translate(-prop.width() * 8, 0, 0));
//            shadow.getChildren().add(propNode);
//        }
        //shadow.getTransforms().add(new Shear(0.25f, 0.25f));
        props.getChildren().add(shadow);
        for (TileProp prop : propCache.asNullable()) {
            Node propNode = createRectangleScaled(prop.image(), WorldView.WORLD_ANGLE);
            propNode.setTranslateX(prop.x() * 16);
            propNode.setTranslateZ(-((prop.y() + prop.height()) * 16));
            props.getChildren().add(propNode);
        }
        return props;
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
