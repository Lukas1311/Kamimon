package de.uniks.stpmon.k.utils;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import javafx.scene.Node;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * Utility class for mesh operations.
 * This class is not intended to be instantiated.
 */
public class MeshUtils {

    /**
     * Disposes the mesh of the given node. This is necessary to avoid memory leaks.
     * JavaFX does not dispose the mesh automatically sometimes.
     *
     * @param node The node to dispose the mesh of.
     */
    public static void disposeMesh(Node node) {
        if (!(node instanceof MeshView mesh)) {
            return;
        }
        mesh.setMesh(null);
        if (mesh.getMaterial() instanceof PhongMaterial phongMaterial) {
            phongMaterial.setDiffuseMap(null);
        }
        mesh.setMaterial(null);
        SceneHelper.setAllowPGAccess(true);
        NodeHelper.updatePeer(mesh);
        SceneHelper.setAllowPGAccess(false);
    }

    public static MeshView createPlane(int l, int b) {
        TriangleMesh mesh = new TriangleMesh();

        float[] vertices = {
                -l / 2f, 0, -b / 2f,
                l / 2f, 0, -b / 2f,
                -l / 2f, 0, b / 2f,
                l / 2f, 0, b / 2f
        };
        float uPadding = (1 / (1024f));
        float vPadding = (1 / (1024f));
        float[] texCoords = {
                0f + uPadding, 1f - vPadding,
                1f - uPadding, 1f - vPadding,
                0f + uPadding, 0f + vPadding,
                1f - uPadding, 0f + vPadding
        };
        int[] faces = {
                0, 0, 1, 1, 2, 2,
                2, 2, 1, 1, 3, 3
        };

        mesh.getPoints().setAll(vertices);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        return new MeshView(mesh);
    }

    public static MeshView createRectangle(int w, int h) {
        TriangleMesh mesh = new TriangleMesh();

        float[] vertices = {
                -w / 2f, 0, 0,
                w / 2f, 0, 0,
                -w / 2f, h, 0,
                w / 2f, h, 0
        };
        // Offset to reduce texture bleeding
        float uPadding = (1 / (512f + 32f)) * (16.0f / (w * 0.15f));
        float vPadding = (1 / (512f - 256f)) * (32.0f / (h * 0.15f));
        float[] texCoords = {
                0f + uPadding, 0f + vPadding,
                1f - uPadding, 0f + vPadding,
                0f + uPadding, 1f - vPadding,
                1f - uPadding, 1f - vPadding
        };
        int[] faces = {
                3, 3, 1, 1, 2, 2,
                2, 2, 1, 1, 0, 0,
                0, 0, 1, 1, 2, 2,
                2, 2, 1, 1, 3, 3
        };

        mesh.getPoints().setAll(vertices);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        return new MeshView(mesh);
    }

}
