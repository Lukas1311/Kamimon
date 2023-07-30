package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.world.RouteData;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
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
import javax.inject.Provider;
import javax.inject.Singleton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Singleton
public class MapOverviewController extends ToastedController {

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
    TrainerStorage trainerStorage;
    @Inject
    RegionService regionService;
    @Inject
    TextDeliveryService textDeliveryService;
    @Inject
    WorldRepository worldRepository;
    @Inject
    Provider<IngameController> ingameController;

    private Shape activeShape;
    private Region currentRegion;
    private Set<String> visitedAreaIds = new HashSet<>();


    @Inject
    public MapOverviewController() {
    }

    @Override
    public void init() {
        super.init();
        currentRegion = regionStorage.getRegion();
        visitedAreaIds = trainerStorage.getTrainer().visitedAreas();
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
                data -> {
                    subscribe(
                            regionService.getAreas(currentRegion._id()),
                            areas -> {
                                renderMapDetails(data, filterVisitedAreas(areas));
                            },
                            this::handleError
                    );
                    
                }
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

    private HashMap<String, Boolean> filterVisitedAreas(List<Area> areas) {
        HashMap<String, Boolean> visitedAreas = new HashMap<>();
        for (Area area : areas) {
            boolean hasSpawn = false;
            if (visitedAreaIds.contains(area._id())) {
                List<Property> properties = area.map().properties();
                if (!(properties == null)) {
                    for (Property prop : properties) {
                        if (prop.name().equals("Spawn")) {
                            hasSpawn = true;
                            break;
                        }
                    }
                }
                visitedAreas.put(area.name(), hasSpawn);
            }
        }
        return visitedAreas;
    }

    private void renderMapDetails(List<RouteData> routeListData, HashMap<String, Boolean> visitedAreas) {

        double originalHeight = mapImageView.getImage().getHeight();
        double scaledHeight = mapImageView.getFitHeight();
        double scaledWidth = mapImageView.getLayoutBounds().getWidth();
        double scaleRatio = scaledHeight / originalHeight;

        double offsetX = (mapStackPane.getWidth() - scaledWidth) / 2.0;

    
        routeListData.forEach(routeData -> {
            boolean visited = visitedAreas.containsKey(routeData.routeText().name());
            if (!routeData.polygon().isEmpty()) {
                Polygon polygon = new Polygon();
                for (PolygonPoint point : routeData.polygon()) {
                    polygon.getPoints().addAll(
                            (double) (routeData.x() + point.x()) * scaleRatio + offsetX,
                            (double) (routeData.y() + point.y()) * scaleRatio
                    );
                }
                addDetailShape(polygon, routeData, visited);
                return;
            }
            if (routeData.width() == 0 || routeData.height() == 0) {
                return;
            }
            Rectangle rectangle = new Rectangle(
                routeData.x() * scaleRatio + offsetX,
                routeData.y() * scaleRatio,
                routeData.width() * scaleRatio,
                routeData.height() * scaleRatio
            );
            addDetailShape(rectangle, routeData, visited);
        });
    }

    private void addDetailShape(Shape shape, RouteData routeData, boolean isVisited) {
        shape.setId("detail_" + routeData.id());
        
        if (isVisited) {
            shape.setFill(Color.TRANSPARENT);
            shape.setOpacity(0);
        } else {
            shape.setFill(Color.SILVER);
            shape.setOpacity(0.95);

            BoxBlur blur = new BoxBlur();
            blur.setWidth(5);
            blur.setHeight(5);
            blur.setIterations(3);
            shape.setEffect(blur);
        }



        highlightPane.getChildren().add(shape);


        shape.setOnMouseClicked(event -> {

            if (activeShape != null && !activeShape.equals(shape)) {
                activeShape.setStroke(null);
                shape.setOpacity(1);
            }

            if (isVisited) {
                areaNameLabel.setText(routeData.routeText().name());
                if (routeData.routeText().description() != "") {
                    regionDescription.setText(routeData.routeText().description());
                } else {
                    regionDescription.setText("Here could be your advertisement.");
                }
                
            } else {
                areaNameLabel.setText("???");
                regionDescription.setText("???");
            }

            // set the newly clicked shape as the active shape
            activeShape = shape;
        });

        shape.setOnMouseEntered(event -> {
            if (activeShape == shape) {
                return;
            }
            if (isVisited) {
                shape.setOpacity(0.75);
            }
            shape.setStroke(Color.WHITESMOKE);
            shape.setStrokeWidth(3);
        });

        shape.setOnMouseExited(event -> {
            if (activeShape == shape) {
                return;
            }
            if (isVisited) {
                // "hidden" areas will stay hidden
                shape.setOpacity(0);
            }
            shape.setStroke(null);
        });
    }
}
