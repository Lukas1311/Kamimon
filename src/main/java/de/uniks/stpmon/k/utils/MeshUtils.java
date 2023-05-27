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
                -w / 2f, -h / 2f, 0,
                w / 2f, -h / 2f, 0,
                -w / 2f, h / 2f, 0,
                w / 2f, h / 2f, 0
        };
        float[] texCoords = {
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f
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
