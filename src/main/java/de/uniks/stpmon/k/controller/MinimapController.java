package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.world.TextureSetService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import javax.inject.Provider;
import java.awt.image.BufferedImage;

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
    Provider<MapOverviewController> mapOverviewControllerProvider;
    @Inject
    TextureSetService textureSetService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;

    private Image map;
    private final static int TILE_SIZE = 16;

    @Inject
    public MinimapController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        Area currentArea = regionStorage.getArea();
        miniMap.setPreserveRatio(false);
        miniMap.setClip(new Circle(75, 75, 75));
        if (currentArea != null) {
            subscribe(
                    textureSetService.createMap(currentArea),
                    tileMap -> {
                        BufferedImage renderedMap = tileMap.renderMap();
                        map = SwingFXUtils.toFXImage(renderedMap, null);
                        miniMap.setImage(map);
                    }
            );
        }

        subscribe(
                trainerStorage.onTrainer(),
                trainer -> {
                    if (trainer != null) {
                        int x = trainer.x() * TILE_SIZE - 144;
                        int y = trainer.y() * TILE_SIZE - 144;
                        Rectangle2D viewPortRect = new Rectangle2D(x, y, 300, 300);
                        miniMap.setViewport(viewPortRect);

                        int direction = trainer.direction();
                        int rotation = switch (direction) {
                            case 0 -> 90;
                            case 2 -> 270;
                            case 3 -> 180;
                            default -> 0;
                        };
                        playerDart.setStyle("-fx-rotate: " + rotation + "deg;");
                    }
                });


        return parent;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void onDestroy(Runnable action) {
        super.onDestroy(action);
    }
}

