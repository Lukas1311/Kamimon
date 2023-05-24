package de.uniks.stpmon.k.controller.map;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TileMapService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldController extends Controller {

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
        subscribe(regionService.getRegion("645e32c6866ace359554a7ec").map((region) ->
                        tileMapService.renderImage(region)),
                (image) -> {
                    imageView.setImage(SwingFXUtils.toFXImage(image, null));
                },
                (error) -> {
                    System.out.println("Error loading image");
                    error.printStackTrace();
                });
    }

    @Override
    public Parent render() {

        // Create an ImageView with the loaded image
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(app.getStage()
                .widthProperty());
        imageView.fitHeightProperty().bind(app.getStage()
                .heightProperty()
                .subtract(80));

        Group root = new Group(imageView);
        root.setCache(true);
        return root;
    }
}
