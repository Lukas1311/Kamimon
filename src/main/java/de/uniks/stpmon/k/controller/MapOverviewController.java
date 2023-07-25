package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.world.RouteData;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
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
    VBox mapContainer;
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
    @Inject
    Provider<IngameController> ingameController;

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
        // System.out.println("test");
        final Parent parent = super.render();

        // center the region name label horizontally
        AnchorPane.setLeftAnchor(regionNameLabel, 0.0);
        AnchorPane.setRightAnchor(regionNameLabel, 0.0);

        // closeButton.setOnAction(click -> {
        //     ingameController.get().closeMap();
        //     System.out.println("close clicked");
        // });

        BorderStroke borderStroke = new BorderStroke(Color.BLACK,
        BorderStrokeStyle.SOLID, null, new BorderWidths(1));
        Border border = new Border(borderStroke);

        // mapContainer.setBorder(border);
        // mapStackPane.setBorder(border);
        // highlightPane.setBorder(border);

        loadBgImage(mapOverviewHolder, "mapOverview_v2.png");

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
                        //loadBgImage(mapContainer, map);
                        mapImageView.setImage(map);
                        mapImageView.setFitHeight(height);
                        mapImageView.setFitWidth(width);
                    }, this::handleError
            );
            // System.out.println("map container: " + mapContainer.heightProperty().get());
            // System.out.println("map stackpane: " + mapStackPane.heightProperty().get());
            // System.out.println("map image view: " + mapImageView.getFitHeight());

            // mapStackPane.minHeightProperty().bind(mapContainer.heightProperty());
            // mapStackPane.maxHeightProperty().bind(mapContainer.heightProperty());
            // highlightPane.minHeightProperty().bind(mapImageView.fitHeightProperty());
            // highlightPane.maxHeightProperty().bind(mapImageView.fitHeightProperty());
                //mapOverviewHolder.widthProperty().multiply(0.6),
                //mapOverviewHolder.heightProperty().multiply(MAP_OVERVIEW_SCALE)
            );
            // mapStackPane.scaleXProperty().bind(binding.map(v -> v.doubleValue() / originalWidth));
            // mapStackPane.scaleYProperty().bind(binding.map(v -> v.doubleValue() / originalHeight));
            //textFlowRegionDescription.prefHeightProperty().bind(mapImageView.fitHeightProperty());
            //textFlowRegionDescription.prefWidthProperty().bind(mapImageView.fitWidthProperty());
            //textFlowRegionDescription.prefWidthProperty().bind(mapOverviewHolder.widthProperty());

            // highlightPane.scaleXProperty().bind(Bindings.createDoubleBinding(
            //     () -> mapImageView.getFitWidth() / originalWidth,
            //     mapImageView.fitWidthProperty()
            // ));
            // highlightPane.scaleYProperty().bind(Bindings.createDoubleBinding(
            //         () -> mapImageView.getFitHeight() / originalHeight,
            //         mapImageView.fitHeightProperty()
            // ));
        }

        //Window parentWindow = app.getStage().getScene().getWindow();
        // mapOverviewHolder.prefWidthProperty().bind(parentWindow.widthProperty().multiply(MAP_OVERVIEW_SCALE));
        // mapOverviewHolder.prefHeightProperty().bind(parentWindow.heightProperty().multiply(MAP_OVERVIEW_SCALE));

        // mapImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> updateShapePositions());
        // mapImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> updateShapePositions());


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
    }

    // private void updateShapePositions(Node shape) {
    //     double originalWidth = currentRegion.map().width() * TILE_SIZE;
    //     double originalHeight = currentRegion.map().height() * TILE_SIZE;
    //     double scaledWidth = mapImageView.getFitWidth();
    //     double scaledHeight = mapImageView.getFitHeight();
    //     double widthRatio = scaledWidth / originalWidth;
    //     double heightRatio = scaledHeight / originalHeight;

    //     double offsetX = (scaledWidth - originalWidth * widthRatio) / 2;
    //     double offsetY = (scaledHeight - originalHeight * heightRatio) / 2;

    //     shape.setTranslateX(routeData.x() * widthRatio + offsetX);
    //     shape.setTranslateY(routeData.y() * heightRatio + offsetY);

    //     // for (Node shape : highlightPane.getChildren()) {
    //         if (shape instanceof Rectangle) {
    //             // Update position for Rectangle
    //             Rectangle rectangle = (Rectangle) shape;
    //             double originalX = rectangle.getX(); // Get the original X coordinate of the rectangle
    //             double originalY = rectangle.getY(); // Get the original Y coordinate of the rectangle
    //             double newX = originalX * widthRatio;
    //             double newY = originalY * heightRatio;
    //             rectangle.setX(newX);
    //             rectangle.setY(newY);
    //         } else if (shape instanceof Polygon) {
    //             // Update position for Polygon
    //             Polygon polygon = (Polygon) shape;
    //             ObservableList<Double> points = polygon.getPoints();
    //             for (int i = 0; i < points.size(); i += 2) {
    //                 double originalX = points.get(i); // Get the original X coordinate of the point
    //                 double originalY = points.get(i + 1); // Get the original Y coordinate of the point
    //                 double newX = originalX * widthRatio;
    //                 double newY = originalY * heightRatio;
    //                 points.set(i, newX);
    //                 points.set(i + 1, newY);
    //             }
    //         }
    //     // }
    // }

    private void renderMapDetails(List<RouteData> routeListData) {
        double originalWidth = currentRegion.map().width() * TILE_SIZE;
        double originalHeight = currentRegion.map().height() * TILE_SIZE;
        double scaledWidth = mapImageView.getFitWidth();
        double scaledHeight = mapImageView.getFitHeight();
        double widthRatio = scaledWidth / originalWidth;
        double heightRatio = scaledHeight / originalHeight;
        double containerHeight = mapContainer.getHeight();
        double containerWidth = mapContainer.getWidth();

        double offsetX = (containerWidth - scaledWidth) / 2.0;
        double offsetY = (containerHeight - scaledHeight) / 2.0;
        System.out.println("test");
    
        routeListData.forEach(routeData -> {
            if (!routeData.polygon().isEmpty()) {
                Polygon polygon = new Polygon();
                for (PolygonPoint point : routeData.polygon()) {
                    polygon.getPoints().addAll(
                        Double.valueOf(routeData.x() * widthRatio + point.x() * widthRatio) + offsetX,
                        Double.valueOf(routeData.y() * heightRatio + point.y() * heightRatio)
                    );
                }
                addDetailShape(polygon, routeData);
                return;
            }
            if (routeData.width() == 0 || routeData.height() == 0) {
                return;
            }
            Rectangle rectangle = new Rectangle(
                routeData.x() * widthRatio + offsetX,
                routeData.y() * heightRatio,
                routeData.width() * widthRatio,
                routeData.height() * heightRatio
            );
            addDetailShape(rectangle, routeData);
        });
    }

    private void addDetailShape(Shape shape, RouteData routeData) {
        shape.setId("detail_" + routeData.id());
        shape.setFill(Color.TRANSPARENT);
        shape.setOpacity(0);
        shape.setStroke(Color.WHITESMOKE);
        shape.setStrokeWidth(3);
        // System.out.println("img view fit width: " + mapImageView.getFitWidth());
        // System.out.println("img width: " + mapImageView.getImage().getWidth());

        highlightPane.getChildren().add(shape);
        // updateShapePositions(shape);

        // shape.scaleXProperty().bind(mapImageView.fitWidthProperty().divide(mapImageView.getImage().getWidth()));
        // shape.scaleYProperty().bind(mapImageView.fitHeightProperty().divide(mapImageView.getImage().getWidth()));
        // double originalWidth = 512;
        // double originalHeight = 512;
        // double scaledWidth = mapImageView.getFitWidth();
        // double scaledHeight = mapImageView.getFitHeight();
        // double widthRatio = scaledWidth / originalWidth;
        // double heightRatio = scaledHeight / originalHeight;
        // double offsetX = routeData.x() * widthRatio;
        // double offsetY = routeData.y() * heightRatio;
    
        // // Translate the shape to the correct position on top of the map
        // shape.setTranslateX(offsetX);
        // shape.setTranslateY(offsetY);


        shape.setOnMouseClicked(event -> {
            regionDescription.setText(routeData.routeText().name() + ":\n" + routeData.routeText().description());
            if (activeShape != null) {
                activeShape.setOpacity(0);
            }
            shape.setOpacity(1);
            activeShape = shape;
            // System.out.println("map container: " + mapContainer.heightProperty().get());
            // System.out.println("map stackpane: " + mapStackPane.heightProperty().get());
            System.out.println("map image view h: " + mapImageView.getFitHeight());
            System.out.println("map image view w: " + mapImageView.getFitWidth());
            System.out.println("map image h: " + mapImageView.getImage().getHeight());
            System.out.println("map image w: " + mapImageView.getImage().getWidth());
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
