package de.uniks.stpmon.k.controller.map;

import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.TileMapService;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class WorldController extends Controller {
    private static final double IMAGE_WIDTH = 200.0;
    private static final double IMAGE_HEIGHT = 200.0;

    @Inject
    protected TileMapService tileMapService;

    @Inject
    public WorldController() {
    }

    @Override
    public void init() {
        super.init();
        try {
            tileMapService.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Parent render() {
        // Load the image
        Image image = new Image(Main.class.getResource("test.png").toString());

        // Create an ImageView with the loaded image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(app.getStage().widthProperty());
        imageView.fitHeightProperty().bind(app.getStage().heightProperty());

        Group root = new Group(imageView);
        //root.setEffect(perspectiveTrasform);
        root.setCache(true);
        return root;
    }
}
