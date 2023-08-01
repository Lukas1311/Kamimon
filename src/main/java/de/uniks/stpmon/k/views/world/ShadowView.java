package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;
import de.uniks.stpmon.k.service.world.PreparationService;
import de.uniks.stpmon.k.utils.MeshUtils;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

import static de.uniks.stpmon.k.utils.ImageUtils.scaledImageFX;

@Singleton
public class ShadowView extends WorldViewable {

    @Inject
    protected WorldRepository repository;
    @Inject
    protected PreparationService preparationService;
    private MeshView shadow;

    @Inject
    public ShadowView() {
    }

    @Override
    public Node render() {
        SingleCache<BufferedImage> imageCache = repository.shadowImage();
        subscribe(repository.shadowImage().onValue(), (shadowImage) -> {
            if (shadowImage.isEmpty()) {
                return;
            }
            shadow.setMaterial(createMaterial(scaledImageFX(shadowImage.get(),
                    effectContext.getTextureScale())));
        });
        if (imageCache.isEmpty()) {
            return new Group();
        }
        BufferedImage mapImage = imageCache.asNullable();
        shadow = createPlaneScaled(mapImage);
        shadow.setId("shadow");
        Bounds bounds = shadow.getBoundsInLocal();
        shadow.setTranslateX(bounds.getWidth() / 2);
        shadow.setTranslateZ(-bounds.getDepth() / 2 + 6);
        shadow.setTranslateY(-0.35);
        return shadow;
    }

    @Override
    public void updateShadow(float factor) {
        repository.shadowImage().setValue(
                preparationService.createShadows(factor));
    }

    @Override
    public void destroy() {
        super.destroy();
        if (shadow != null) {
            MeshUtils.disposeMesh(shadow);
            shadow = null;
        }
    }
}
