package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
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
import javafx.scene.shape.Polygon;
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

    private final static double MAP_OVERVIEW_SCALE = 0.8; // scale the map container to 80% of screen
    private final static int TILE_SIZE = 16;
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
            int width = currentRegion.map().width() * TILE_SIZE;
            int height = currentRegion.map().height() * TILE_SIZE;
            subscribe(
                    textureSetService.createMap(currentRegion),
                    tileMap -> {
                        BufferedImage renderedMap = tileMap.renderMap();
                        map = SwingFXUtils.toFXImage(renderedMap, null);
                        mapImageView.setImage(map);
                        mapImageView.setFitHeight(height);
                        mapImageView.setFitWidth(width);
                    }, this::handleError
            );
            NumberBinding binding = Bindings.min(mapOverviewContent.widthProperty().multiply(0.6),
                    mapOverviewContent.heightProperty().multiply(MAP_OVERVIEW_SCALE));
            mapStackPane.scaleXProperty().bind(binding.map(v -> v.doubleValue() / width));
            mapStackPane.scaleYProperty().bind(binding.map(v -> v.doubleValue() / height));
        }

        Window parentWindow = app.getStage().getScene().getWindow();
        mapOverviewContent.prefWidthProperty().bind(parentWindow.widthProperty().multiply(MAP_OVERVIEW_SCALE));
        mapOverviewContent.prefHeightProperty().bind(parentWindow.heightProperty().multiply(MAP_OVERVIEW_SCALE));

        if (currentRegion.map() != null) {
            subscribe(
                    textDeliveryService.getRouteData(currentRegion),
                    routeListData -> routeListData.forEach(routeData -> {
                        if (!routeData.polygon().isEmpty()) {
                            Polygon polygon = new Polygon();
                            for (PolygonPoint point : routeData.polygon()) {
                                polygon.getPoints().addAll(Double.valueOf(routeData.x() + point.x()), Double.valueOf(routeData.y() + point.y()));
                            }
                            polygon.setFill(Paint.valueOf("#ffffff"));
                            polygon.setOpacity(0.1);
                            polygon.setOnMouseClicked(event -> regionDescription.setText(routeData.routeText().description()));
                            highlightPane.getChildren().add(polygon);
                            return;
                        }
                        if (routeData.width() == 0 || routeData.height() == 0) {
                            return;
                        }
                        Rectangle rectangle = new Rectangle(routeData.x(), routeData.y(), routeData.width(), routeData.height());
                        rectangle.setFill(Paint.valueOf("#ffffff"));
                        rectangle.setOpacity(0.1);
                        highlightPane.getChildren().add(rectangle);
                        rectangle.setOnMouseClicked(event -> regionDescription.setText(routeData.routeText().description()));
                    }));

        }
        return parent;
    }
}
