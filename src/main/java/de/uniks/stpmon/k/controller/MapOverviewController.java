package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;


@Singleton
public class MapOverviewController extends ToastedController {

    @FXML
    public Pane highlightPane;
    @FXML
    public StackPane mapStackPane;
    @FXML
    BorderPane mapOverviewContent;
    @FXML
    Label regionNameLabel;
    @FXML
    public Button closeButton;
    @FXML
    ImageView mapImageView;
    @FXML
    VBox mapContainer;
    @FXML
    Text regionDescription;

    @Inject
    TextureSetService textureSetService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TextDeliveryService textDeliveryService;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    RegionService regionService;

    private final double MAP_OVERVIEW_SCALE = 0.8; // scale the map container to 80% of screen
    private Image map;

    @Inject
    public MapOverviewController() {
    }

    @Override
    public void init() {
        super.init();
    }


    @Override
    public Parent render() {
        final Parent parent = super.render();
        Region currentRegion = regionStorage.getRegion();
        regionNameLabel.setText(currentRegion.name());
        if (currentRegion.map() != null) {
            subscribe(
                    textureSetService.createMap(currentRegion),
                    tileMap -> {
                        BufferedImage renderedMap = tileMap.renderMap();
                        map = SwingFXUtils.toFXImage(renderedMap, null);
                        mapImageView.setImage(map);
                        mapImageView.fitWidthProperty().bind(mapOverviewContent.widthProperty().multiply(0.6));
                        mapImageView.fitHeightProperty().bind(mapOverviewContent.heightProperty().multiply(MAP_OVERVIEW_SCALE));
                        mapContainer.setPrefSize(mapImageView.getFitWidth(), mapImageView.getFitHeight());
                    }, this::handleError
            );
        }

        Window parentWindow = app.getStage().getScene().getWindow();
        mapOverviewContent.prefWidthProperty().bind(parentWindow.widthProperty().multiply(MAP_OVERVIEW_SCALE));
        mapOverviewContent.prefHeightProperty().bind(parentWindow.heightProperty().multiply(MAP_OVERVIEW_SCALE));

        highlightPane.maxWidthProperty().bind(mapImageView.fitWidthProperty());
        highlightPane.maxHeightProperty().bind(mapImageView.fitHeightProperty());

        mapStackPane.setScaleX(1.1);
        mapStackPane.setScaleY(1.1);


        if (currentRegion.map() != null) {
            subscribe(
                    textDeliveryService.getRouteData(currentRegion),
                    routeListData -> routeListData.forEach(routeData -> {

                        Rectangle rectangle = new Rectangle(routeData.x(), routeData.y(), routeData.width(), routeData.height());
                        rectangle.setFill(Paint.valueOf("#ffffff"));
                        rectangle.setOpacity(0.3);
                        highlightPane.getChildren().add(rectangle);
                        rectangle.setOnMouseClicked(event -> regionDescription.setText(routeData.routeText().description()));
                    }));

        }
        return parent;
    }
}
