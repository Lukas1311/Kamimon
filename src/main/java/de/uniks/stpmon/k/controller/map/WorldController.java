package de.uniks.stpmon.k.controller.map;

import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TileMapService;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldController extends Controller {
    private static final double IMAGE_WIDTH = 200.0;
    private static final double IMAGE_HEIGHT = 200.0;

    @Inject
    protected TileMapService tileMapService;
    @Inject
    protected RegionService regionService;

    @Inject
    public WorldController() {
    }

    ImageView imageView = new ImageView();

    @Override
    public void init() {
        super.init();
        subscribe(regionService.getRegion("0").map((region) ->
                        tileMapService.loadImage(region)),
                (image) -> {
                    // imageView.setImage(SwingFXUtils.toFXImage(image, null));
                },
                (error) -> {
                    System.out.println("Error loading image");
                    error.printStackTrace();
                });
    }

    @Override
    public Parent render() {
        // Load the image
        Image image = new Image(Main.class.getResource("test.png").toString());

        // Create an ImageView with the loaded image
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
