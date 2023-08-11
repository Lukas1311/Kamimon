package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.utils.MeshUtils;
import de.uniks.stpmon.k.world.ShadowTransform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

import static de.uniks.stpmon.k.utils.ImageUtils.scaledImageFX;
import static de.uniks.stpmon.k.utils.ImageUtils.toFXImage;

public abstract class WorldViewable extends Viewable {

    @Inject
    protected EffectContext effectContext;

    @SuppressWarnings("unused")
    public abstract Node render();

    @SuppressWarnings("unused")
    public void updateShadow(ShadowTransform transform) {
    }

    protected void setScaledMaterial(MeshView mesh, BufferedImage image) {
        mesh.setMaterial(createMaterial(ImageUtils.toFXImage(image)));
    }

    protected void updateImage(MeshView mesh, BufferedImage image) {
        if (mesh.getMaterial() instanceof PhongMaterial phongMaterial) {
            phongMaterial.setDiffuseMap(ImageUtils.toFXImage(image));
        }
    }

    protected void updateMaterialOpacity(MeshView mesh, float opacity) {
        if (mesh.getMaterial() instanceof PhongMaterial phongMaterial) {
            phongMaterial.setDiffuseColor(Color.TRANSPARENT.interpolate(Color.WHITE, opacity));
        }
    }

    protected Material createMaterial(Image image) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(image);
        return material;
    }

    protected MeshView createPlaneScaled(BufferedImage image) {
        Image scaledimage = scaledImageFX(image, effectContext.getTextureScale());

        return createPlane(scaledimage,
                (int) (scaledimage.getWidth() / effectContext.getTextureScale()),
                (int) (scaledimage.getHeight() / effectContext.getTextureScale()));
    }

    protected MeshView createPlane(Image image, int width, int height) {
        MeshView floor = MeshUtils.createPlane(width, height);
        floor.setDrawMode(DrawMode.FILL);
        floor.setCullFace(CullFace.BACK);
        floor.setMaterial(createMaterial(image));

        return floor;
    }

    @SuppressWarnings("SameParameterValue")
    protected MeshView createRectangleScaled(BufferedImage image, int angle) {
        Image scaledimage = scaledImageFX(image, effectContext.getTextureScale());
        return createRectangle(scaledimage,
                (int) (scaledimage.getWidth() / effectContext.getTextureScale()),
                (int) (scaledimage.getHeight() / effectContext.getTextureScale()), angle);
    }

    @SuppressWarnings("SameParameterValue")
    protected MeshView createRectangleScaled(BufferedImage image, int width, int height, int angle) {
        Image scaledimage = scaledImageFX(image, effectContext.getTextureScale());
        return createRectangle(scaledimage,
                width,
                height, angle);
    }

    @SuppressWarnings("SameParameterValue")
    protected MeshView createRectangle(BufferedImage image, int angle) {
        return createRectangle(toFXImage(image),
                image.getWidth(), image.getHeight(), angle);
    }

    protected MeshView createRectangle(Image image, int width, int height, int angle) {
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
