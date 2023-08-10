package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.utils.MeshUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

import static de.uniks.stpmon.k.constants.TileConstants.CHUNK_SIZE;

@Singleton
public class FloorView extends WorldViewable {

    @Inject
    protected WorldRepository repository;
    private Group chunks;

    @Inject
    public FloorView() {
    }

    @Override
    public Node render() {
        chunks = new Group();
        BufferedImage[][] images = repository.getChunks();
        for (int x = 0; x < images.length; x++) {
            for (int y = 0; y < images[x].length; y++) {
                BufferedImage image = images[x][y];
                if (image != null) {
                    MeshView mesh = createPlaneScaled(image);
                    mesh.setTranslateX((x + 0.5f) * CHUNK_SIZE);
                    mesh.setTranslateZ(-(y + 0.5f) * CHUNK_SIZE);
                    chunks.getChildren().add(mesh);
                }
            }
        }
        return chunks;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (chunks != null) {
            for (Node node : chunks.getChildren()) {
                MeshUtils.disposeMesh(node);
            }
            chunks.getChildren().clear();
            chunks = null;
        }
    }
}
