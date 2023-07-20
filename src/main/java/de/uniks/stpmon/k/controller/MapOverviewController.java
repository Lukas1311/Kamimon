package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.world.RouteData;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;


@Singleton
public class MapOverviewController extends ToastedController {
    private final static double MAP_OVERVIEW_SCALE = 0.8; // scale the map container to 80% of screen
    private final static int TILE_SIZE = 16;

    @FXML
    public Pane highlightPane;
    @FXML
    public StackPane mapStackPane;
    @FXML
    public TextFlow textFlowRegionDescription;
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
    RegionStorage regionStorage;
    @Inject
    TextDeliveryService textDeliveryService;
    @Inject
    WorldRepository worldRepository;
    @Inject
    Provider<IngameController> ingameController;

    private Shape activeShape;

    @Inject
    public MapOverviewController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        closeButton.setOnAction(click -> ingameController.get().closeMap());
        Region currentRegion = regionStorage.getRegion();
        regionNameLabel.setText(currentRegion.name());
        if (currentRegion.map() != null) {
            int width = currentRegion.map().width() * TILE_SIZE;
            int height = currentRegion.map().height() * TILE_SIZE;
            subscribe(
                    worldRepository.regionMap().onValue(),
                    renderedMap -> {
                        if (renderedMap.isEmpty()) {
                            return;
                        }
                        Image map = SwingFXUtils.toFXImage(renderedMap.get(), null);
                        mapImageView.setImage(map);
                        mapImageView.setFitHeight(height);
                        mapImageView.setFitWidth(width);
                    }, this::handleError
            );
            NumberBinding binding = Bindings.min(mapOverviewContent.widthProperty().multiply(0.6),
                    mapOverviewContent.heightProperty().multiply(MAP_OVERVIEW_SCALE));
            mapStackPane.scaleXProperty().bind(binding.map(v -> v.doubleValue() / width));
            mapStackPane.scaleYProperty().bind(binding.map(v -> v.doubleValue() / height));
            textFlowRegionDescription.prefWidthProperty().bind(mapOverviewContent.widthProperty());
        }

        Window parentWindow = app.getStage().getScene().getWindow();
        mapOverviewContent.prefWidthProperty().bind(parentWindow.widthProperty().multiply(MAP_OVERVIEW_SCALE));
        mapOverviewContent.prefHeightProperty().bind(parentWindow.heightProperty().multiply(MAP_OVERVIEW_SCALE));

        if (currentRegion.map() != null) {
            subscribe(
                    textDeliveryService.getRouteData(currentRegion),
                    this::renderMapDetails);
        }

        return parent;
    }

    private void renderMapDetails(List<RouteData> routeListData) {
        routeListData.forEach(routeData -> {
            if (!routeData.polygon().isEmpty()) {
                Polygon polygon = new Polygon();
                for (PolygonPoint point : routeData.polygon()) {
                    polygon.getPoints().addAll(Double.valueOf(routeData.x() + point.x()),
                            Double.valueOf(routeData.y() + point.y()));
                }
                addDetailShape(polygon, routeData);
                return;
            }
            if (routeData.width() == 0 || routeData.height() == 0) {
                return;
            }
            Rectangle rectangle = new Rectangle(routeData.x(), routeData.y(), routeData.width(), routeData.height());
            addDetailShape(rectangle, routeData);
        });
    }

    private void addDetailShape(Shape shape, RouteData routeData) {
        shape.setId("detail_" + routeData.id());
        shape.setFill(Color.TRANSPARENT);
        shape.setOpacity(0);
        shape.setStroke(Color.WHITESMOKE);
        shape.setStrokeWidth(3);
        highlightPane.getChildren().add(shape);

        shape.setOnMouseClicked(event -> {
            regionDescription.setText(routeData.routeText().name() + ":\n" + routeData.routeText().description());
            if (activeShape != null) {
                activeShape.setOpacity(0);
            }
            shape.setOpacity(1);
            activeShape = shape;
        });

        shape.setOnMouseEntered(event -> {
            if (activeShape == shape) {
                return;
            }
            shape.setOpacity(0.75);
        });
        shape.setOnMouseExited(event -> {
            if (activeShape == shape) {
                return;
            }
            shape.setOpacity(0);
        });
    }
}
