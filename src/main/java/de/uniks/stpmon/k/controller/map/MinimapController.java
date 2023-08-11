package de.uniks.stpmon.k.controller.map;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.inject.Inject;

public class MinimapController extends Controller {

    public static final int VISIBLE_AREA = 550;
    @FXML
    public ImageView miniMap;
    @FXML
    public Polygon playerDart;
    @FXML
    public ImageView miniMapBorder;
    @FXML
    public StackPane miniMapStackPane;
    @FXML
    public Polygon monCenterDart;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    WorldRepository worldRepository;
    @Inject
    TextDeliveryService textDeliveryService;
    Point2D monCenter = Point2D.ZERO;

    private final static int TILE_SIZE = 16;

    @Inject
    public MinimapController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(miniMapBorder, getResourcePath() + "emptyMinimap.png");

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
            subscribe(textDeliveryService.getNextMonCenter(currentArea), (monCenter) -> this.monCenter = monCenter);
        }
        double centerX = playerDart.getBoundsInLocal().getCenterX();
        double centerY = playerDart.getBoundsInLocal().getCenterY();
        Rotate playerRotate = new Rotate(0, centerX, centerY);
        centerX = monCenterDart.getBoundsInLocal().getCenterX();
        Rotate monCenterRotate = new Rotate(0, centerX, 0);
        Translate monCenterPos = new Translate(0, 0);
        playerDart.getTransforms().add(playerRotate);
        monCenterDart.getTransforms().add(monCenterPos);
        monCenterDart.getTransforms().add(monCenterRotate);
        subscribe(
                trainerStorage.onTrainer(),
                trainer -> {
                    if (trainer.isEmpty()) {
                        return;
                    }

                    Point2D playerPos = new Point2D((trainer.get().x() + 0.5f) * TILE_SIZE,
                            (trainer.get().y() + 1f) * TILE_SIZE);
                    updateMonCenterDart(playerPos, monCenterRotate, monCenterPos);

                    int x = (int) playerPos.getX() - VISIBLE_AREA / 2;
                    int y = (int) playerPos.getY() - VISIBLE_AREA / 2;
                    Rectangle2D viewPortRect = new Rectangle2D(x, y, VISIBLE_AREA, VISIBLE_AREA);
                    miniMap.setViewport(viewPortRect);

                    int direction = trainer.get().direction();
                    int rotation = PlayerDirection.values()[direction].getDegrees();
                    playerRotate.setAngle(rotation);
                });


        return parent;
    }

    private void updateMonCenterDart(Point2D playerPos, Rotate monCenterRotate, Translate monCenterPos) {
        if (monCenter == Point2D.ZERO) {
            monCenterDart.setVisible(false);
            return;
        }
        Point2D dir = playerPos.subtract(monCenter).normalize();
        double angle = dir.angle(new Point2D(0, 1));
        if (playerPos.getX() > monCenter.getX()) {
            angle = 360 - angle;
        }
        dir = dir.multiply(-350);
        monCenterRotate.setAngle(angle);
        monCenterPos.setX(dir.getX());
        monCenterPos.setY(dir.getY());
        monCenterDart.setVisible(true);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (miniMap != null) {
            miniMap.getImage().cancel();
            miniMap.setImage(null);
            SceneHelper.setAllowPGAccess(true);
            NodeHelper.updatePeer(miniMap);
            SceneHelper.setAllowPGAccess(false);
            miniMap = null;
        }
        miniMapBorder.setImage(null);
        miniMapBorder = null;
    }

    @Override
    public String getResourcePath() {
        return "map/";
    }
}

