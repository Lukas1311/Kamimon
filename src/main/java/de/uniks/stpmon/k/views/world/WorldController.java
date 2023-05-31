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
import javafx.scene.transform.Translate;

import java.awt.image.BufferedImage;

import static de.uniks.stpmon.k.utils.ImageUtils.scaledImageFX;

public abstract class WorldController extends Viewable {

    public static double IMAGE_SCALE = 4.0;

    public abstract Node render(int angle, PerspectiveCamera camera);

    protected Material createMaterial(Image image) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(image);
        return material;
    }

    protected MeshView createPlaneScaled(BufferedImage image) {
        Image scaledimage = scaledImageFX(image, IMAGE_SCALE);

        return createPlane(scaledimage,
                (int) (scaledimage.getWidth() / IMAGE_SCALE),
                (int) (scaledimage.getHeight() / IMAGE_SCALE));
    }

    protected MeshView createPlane(Image image, int width, int height) {
        MeshView floor = MeshUtils.createPlane(width, height);
        floor.setDrawMode(DrawMode.FILL);
        floor.setCullFace(CullFace.BACK);
        floor.setMaterial(createMaterial(image));

        return floor;
    }

    protected Node createRectangleScaled(String path, int angle) {
        Image scaledimage = scaledImageFX(path, IMAGE_SCALE);
        return createRectangle(scaledimage,
                (int) (scaledimage.getWidth() / IMAGE_SCALE),
                (int) (scaledimage.getHeight() / IMAGE_SCALE), angle);
    }

    protected Node createRectangle(Image image, int width, int height, int angle) {
        MeshView entity = MeshUtils.createRectangle(width, height);
        entity.setDrawMode(DrawMode.FILL);
        entity.setCullFace(CullFace.BACK);
        Rotate rotate = new Rotate(angle, Rotate.X_AXIS);
        // Rotate around the bottom center of the entity
        rotate.setPivotZ(width / 2.0);
        rotate.setPivotZ(0);
        rotate.setPivotY(0);
        entity.getTransforms().addAll(rotate, new Translate(width / 2.0, -height, 0));
        entity.setMaterial(createMaterial(image));
        return entity;
    }
}
