package de.uniks.stpmon.k.controller;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import javax.inject.Inject;

public class MinimapController extends Controller {

    @FXML
    public ImageView miniMap;
    @FXML
    public Polygon playerDart;
    @FXML
    public ImageView miniMapBorder;
    @FXML
    public StackPane miniMapStackPane;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    WorldRepository worldRepository;

    private final static int TILE_SIZE = 16;

    @Inject
    public MinimapController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(miniMapBorder, "emptyMinimap.png");

        Area currentArea = regionStorage.getArea();
        miniMap.setPreserveRatio(false);
        miniMap.setClip(new Circle(75, 75, 75));
        if (currentArea != null) {
            subscribe(
                    worldRepository.minimapImage().onValue(),
                    renderedMap -> {
                        if (renderedMap.isEmpty()) {
                            return;
                        }
                        miniMap.setImage(ImageUtils.toFXImage(renderedMap.get()));
                    }
            );
        }
        double centerX = playerDart.getBoundsInLocal().getCenterX();
        double centerY = playerDart.getBoundsInLocal().getCenterY();
        Rotate rotate = new Rotate(0, centerX, centerY);
        playerDart.getTransforms().add(rotate);
        subscribe(
                trainerStorage.onTrainer(),
                trainer -> {
                    if (trainer.isPresent()) {
                        int x = trainer.get().x() * TILE_SIZE - 144;
                        int y = trainer.get().y() * TILE_SIZE - 144;
                        Rectangle2D viewPortRect = new Rectangle2D(x, y, 300, 300);
                        miniMap.setViewport(viewPortRect);

                        int direction = trainer.get().direction();
                        int rotation = switch (direction) {
                            case 0 -> 90;
                            case 2 -> 270;
                            case 3 -> 180;
                            default -> 0;
                        };
                        rotate.setAngle(rotation);
                    }
                });


        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        miniMap.getImage().cancel();
        miniMap.setImage(null);
        SceneHelper.setAllowPGAccess(true);
        NodeHelper.updatePeer(miniMap);
        SceneHelper.setAllowPGAccess(false);
        miniMap = null;
        miniMapBorder.setImage(null);
        miniMapBorder = null;
    }
}

