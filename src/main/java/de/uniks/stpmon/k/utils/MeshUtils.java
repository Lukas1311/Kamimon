package de.uniks.stpmon.k.utils;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class MeshUtils {

    public static MeshView createPlane(int l, int b) {
        TriangleMesh mesh = new TriangleMesh();

        float[] vertices = {
                -l / 2f, 0, -b / 2f,
                l / 2f, 0, -b / 2f,
                -l / 2f, 0, b / 2f,
                l / 2f, 0, b / 2f
        };
        float[] texCoords = {
                0f, 1f,
                1f, 1f,
                0f, 0f,
                1f, 0f
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
        float uPadding = (1 / (512f + 32f)) * (16.0f / w);
        float vPadding = (1 / (512f - 256f)) * (32.0f / h);
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
