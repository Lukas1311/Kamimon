package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.utils.MeshUtils;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

public abstract class WorldController extends Viewable {

    public abstract Node render(int angle, PerspectiveCamera camera);

    protected Material createMaterial(Image image) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(image);
        return material;
    }

    protected MeshView createPlane(Image image) {
        double height = image.getHeight();
        double width = image.getWidth();
        MeshView floor = MeshUtils.createPlane((int) width * 2, (int) height * 2);
        floor.setDrawMode(DrawMode.FILL);
        floor.setScaleX(0.5);
        floor.setScaleY(0.5);
        floor.setScaleZ(0.5);
        floor.setCullFace(CullFace.BACK);
        floor.setMaterial(createMaterial(image));

        return floor;
    }

    protected MeshView createRectangle(Image image, int angle) {
        MeshView entity = MeshUtils.createRectangle(32, 64);
        entity.setDrawMode(DrawMode.FILL);
        entity.setScaleX(0.5);
        entity.setScaleY(0.5);
        entity.setScaleZ(0.5);
        entity.setCullFace(null);
        entity.setTranslateY(-14);
        entity.setRotationAxis(Rotate.X_AXIS);
        entity.setRotate(angle);
        entity.setMaterial(createMaterial(image));
        return entity;
    }
}
