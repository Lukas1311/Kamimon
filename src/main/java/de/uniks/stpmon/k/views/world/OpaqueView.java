package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.world.ShadowTransform;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import java.util.LinkedList;
import java.util.List;

public abstract class OpaqueView<N extends Node> extends WorldViewable {
    public static final float SHADOW_OPACITY = 0.25f;
    protected N shadowNode;
    protected float shadowOpacity = 0.0f;
    protected Shear shadowShear;
    protected Scale shadowScale;

    protected void createTransforms() {
        shadowShear = new Shear(0, 0, 0, 0);
        shadowScale = new Scale(1.0f, 1.0f, 1.0f);
    }

    protected List<Transform> getTransforms(int height) {
        if (shadowScale == null) {
            createTransforms();
        }
        List<Transform> transforms = new LinkedList<>();
        // Translate to bottom of the entity
        transforms.add(new Translate(0, height, 0));
        transforms.add(shadowShear);
        // Scale at the pivot point
        transforms.add(shadowScale);
        // Translate back to of the entity
        transforms.add(new Translate(0, -height, 0));
        transforms.add(new Translate(0, 0, -0.1 - 0.5 * Math.random()));
        return transforms;
    }

    public void updateShadow(ShadowTransform transform) {
        if (shadowNode == null) {
            return;
        }
        if (transform == null || shadowShear == null || transform.isDisabled()) {
            shadowNode.setVisible(false);
            return;
        }
        shadowNode.setVisible(true);
        shadowOpacity = 1 - transform.timeFactor();
        updateOpacity(shadowNode, shadowOpacity);
        shadowScale.setX(transform.scaleX());
        shadowScale.setY(transform.scaleY());
        shadowShear.setX(transform.shearX());
        shadowShear.setY(transform.shearY());
    }


    protected abstract void updateOpacity(N node, float opacity);
}
