package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.world.RouteData;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;


@Singleton
public class MapOverviewController extends ToastedController {

    public static final double OPACITY_HOVERED = 0.75;
    public static final int OPACITY_SELECTED = 1;
    public static final int OPACITY_DESELECTED = 0;
    @FXML
    StackPane mapStackPane;
    @FXML
    TextFlow textFlowRegionDescription;
    @FXML
    AnchorPane mapOverviewHolder;
    @FXML
    Label regionNameLabel;
    @FXML
    Label areaNameLabel;
    @FXML
    Button closeButton;
    @FXML
    ImageView mapImageView;
    @FXML
    Pane highlightPane;
    @FXML
    Text regionDescription;

    @Inject
    RegionStorage regionStorage;
    @Inject
    TextDeliveryService textDeliveryService;
    @Inject
    WorldRepository worldRepository;

    private Shape activeShape;
    private Region currentRegion;

    @Inject
    public MapOverviewController() {
    }

    @Override
    public void init() {
        super.init();
        currentRegion = regionStorage.getRegion();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        // center the region name label horizontally
        AnchorPane.setLeftAnchor(regionNameLabel, 0.0);
        AnchorPane.setRightAnchor(regionNameLabel, 0.0);

        loadBgImage(mapOverviewHolder, "mapOverview_v2.png");

        regionNameLabel.setText(currentRegion.name());
        if (currentRegion.map() != null) {
            subscribe(
                    worldRepository.regionMap().onValue(),
                    renderedMap -> {
                        if (renderedMap.isEmpty()) {
                            return;
                        }
                        Image map = SwingFXUtils.toFXImage(renderedMap.get(), null);
                        mapImageView.setImage(map);
                    }, this::handleError
            );
            mapImageView.fitHeightProperty().bind(mapStackPane.heightProperty());
        }

        if (currentRegion.map() != null) {
            subscribe(
                    textDeliveryService.getRouteData(currentRegion),
                    this::renderMapDetails
            );
        }

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mapImageView != null) {
            mapImageView.setImage(null);
            mapImageView = null;
        }
        mapStackPane = null;
        textFlowRegionDescription = null;
        mapOverviewHolder = null;
    }

    private void renderMapDetails(List<RouteData> routeListData) {
        double originalHeight = mapImageView.getImage().getHeight();
        double scaledHeight = mapImageView.getFitHeight();
        double scaledWidth = mapImageView.getLayoutBounds().getWidth();
        double scaleRatio = scaledHeight / originalHeight;

        double offsetX = (mapStackPane.getWidth() - scaledWidth) / 2.0;


        routeListData.forEach(routeData -> {
            if (!routeData.polygon().isEmpty()) {
                Polygon polygon = new Polygon();
                for (PolygonPoint point : routeData.polygon()) {
                    polygon.getPoints().addAll(
                            (double) (routeData.x() + point.x()) * scaleRatio + offsetX,
                            (double) (routeData.y() + point.y()) * scaleRatio
                    );
                }
                addDetailShape(polygon, routeData);
                return;
            }
            if (routeData.width() == OPACITY_DESELECTED || routeData.height() == OPACITY_DESELECTED) {
                return;
            }
            Rectangle rectangle = new Rectangle(
                    routeData.x() * scaleRatio + offsetX,
                    routeData.y() * scaleRatio,
                    routeData.width() * scaleRatio,
                    routeData.height() * scaleRatio
            );
            addDetailShape(rectangle, routeData);
        });
    }

    private void addDetailShape(Shape shape, RouteData routeData) {
        shape.setId("detail_" + routeData.id());
        shape.setFill(Color.TRANSPARENT);
        shape.setOpacity(OPACITY_DESELECTED);
        shape.setStroke(Color.WHITESMOKE);
        shape.setStrokeWidth(3);

        highlightPane.getChildren().add(shape);


        shape.setOnMouseClicked(event -> {
            areaNameLabel.setText(routeData.routeText().name());
            regionDescription.setText(routeData.routeText().description());
            if (activeShape != null) {
                activeShape.setOpacity(OPACITY_DESELECTED);
            }
            shape.setOpacity(OPACITY_SELECTED);
            activeShape = shape;
        });

        shape.setOnMouseEntered(event -> {
            if (activeShape == shape) {
                return;
            }
            shape.setOpacity(OPACITY_HOVERED);
        });
        shape.setOnMouseExited(event -> {
            if (activeShape == shape) {
                return;
            }
            shape.setOpacity(OPACITY_DESELECTED);
        });
    }
}
