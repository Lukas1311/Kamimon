package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;
import de.uniks.stpmon.k.service.world.PreparationService;
import de.uniks.stpmon.k.utils.MeshUtils;
import de.uniks.stpmon.k.world.ShadowTransform;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

@Singleton
public class ShadowView extends WorldViewable {

    @Inject
    protected WorldRepository repository;
    @Inject
    protected PreparationService preparationService;
    protected PublishSubject<ShadowTransform> shadowUpdate = PublishSubject.create();
    private MeshView shadow;
    private float opacity = 0.0f;

    @Inject
    public ShadowView() {
    }

    @Override
    public Node render() {
        SingleCache<BufferedImage> imageCache = repository.shadowImage();
//        subscribe(repository.shadowImage().onValue(), (shadowImage) -> {
//            if (shadowImage.isEmpty()) {
//                return;
//            }
//            updateImage(shadow, shadowImage.get());
//            updateOpacity(shadow, opacity);
//        });
        subscribe(shadowUpdate.observeOn(Schedulers.io()).map((transform) -> {
            opacity = 1 - transform.timeFactor();
            return preparationService.createShadows(transform);
        }).observeOn(FX_SCHEDULER), (image) -> {
            updateImage(shadow, image);
            updateMaterialOpacity(shadow, opacity);
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
        shadow.setTranslateY(-0.45);
        return new Group();
    }

    @Override
    public void updateShadow(ShadowTransform transform) {
        if (transform.isDisabled()) {
            shadow.setVisible(false);
            return;
        }
        shadow.setVisible(true);
        shadowUpdate.onNext(transform);
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
