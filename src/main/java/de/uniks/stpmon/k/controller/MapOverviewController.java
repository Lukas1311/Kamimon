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
import javafx.util.Pair;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Singleton
public class MapOverviewController extends ToastedController {

    public static final double OPACITY_BLUR = 0.95;
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
    @FXML
    Button fastTravelButton;

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
    Provider<IngameController> ingameControllerProvider;

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

        fastTravelButton.setText(translateString("fastTravel"));
        fastTravelButton.setVisible(false);

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
                data -> subscribe(
                        regionService.getAreas(currentRegion._id()),
                        areas -> renderMapDetails(data, filterVisitedAreas(areas)),
                        this::handleError
                )
            );
        }

        return parent;
    }

    private void fastTravel(String area) {
        subscribe(trainerService.fastTravel(area),
            // close the map here because its somehow still open after travel
            trainer -> ingameControllerProvider.get().closeMap(),
            this::handleError
        );
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

    private HashMap<String, Pair<String, Boolean>> filterVisitedAreas(List<Area> areas) {
        HashMap<String, Pair<String, Boolean>> visitedAreas = new HashMap<>();
        for (Area area : areas) {
            boolean hasSpawn = false;
            if (visitedAreaIds.contains(area._id())) {
                if (area.map() != null) {
                    List<Property> properties = area.map().properties();
                    if (!(properties == null)) {
                        for (Property prop : properties) {
                            if (prop.name().equals("Spawn")) {
                                hasSpawn = true;
                                break;
                            }
                        }
                    }
                    visitedAreas.put(area.name(), new Pair<String,Boolean>(area._id(), hasSpawn));
                }
            }
        }
        return visitedAreas;
    }

    private void renderMapDetails(List<RouteData> routeListData, HashMap<String, Pair<String, Boolean>> visitedAreas) {

        double originalHeight = mapImageView.getImage().getHeight();
        double scaledHeight = mapImageView.getFitHeight();
        double scaledWidth = mapImageView.getLayoutBounds().getWidth();
        double scaleRatio = scaledHeight / originalHeight;

        double offsetX = (mapStackPane.getWidth() - scaledWidth) / 2.0;


        routeListData.forEach(routeData -> {
            boolean visited = visitedAreas.containsKey(routeData.routeText().name());
            boolean hasSpawn = false;
            String areaId = "";
            if (visited) {
                // getValue returns the "right value" in this case the bool
                hasSpawn = visitedAreas.get(routeData.routeText().name()).getValue();
                if (hasSpawn) {
                    areaId = visitedAreas.get(routeData.routeText().name()).getKey();
                }
            }
            if (!routeData.polygon().isEmpty()) {
                Polygon polygon = new Polygon();
                for (PolygonPoint point : routeData.polygon()) {
                    polygon.getPoints().addAll(
                            (double) (routeData.x() + point.x()) * scaleRatio + offsetX,
                            (double) (routeData.y() + point.y()) * scaleRatio
                    );
                }
                addDetailShape(polygon, routeData, visited, hasSpawn, areaId);
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
            addDetailShape(rectangle, routeData, visited, hasSpawn, areaId);
        });
    }

    private void addDetailShape(Shape shape, RouteData routeData, boolean isVisited, boolean hasSpawn, String areaId) {
        shape.setId("detail_" + routeData.id());
        // set the area id as hidden user data
        shape.setUserData(areaId);
        
        if (isVisited) {
            shape.setFill(Color.TRANSPARENT);
            shape.setOpacity(OPACITY_DESELECTED);
        } else {
            shape.setFill(Color.SILVER);
            shape.setOpacity(OPACITY_BLUR);

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
                fastTravelButton.setVisible(false);
                shape.setOpacity(OPACITY_SELECTED);
            }

            if (isVisited) {
                areaNameLabel.setText(routeData.routeText().name());
                if (!routeData.routeText().description().isEmpty()) {
                    regionDescription.setText(routeData.routeText().description());
                } else {
                    regionDescription.setText("Here could be your advertisement.");
                }
                fastTravelButton.setVisible(hasSpawn);
                fastTravelButton.setOnAction(click -> fastTravel((String) shape.getUserData()));
                
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
                shape.setOpacity(OPACITY_HOVERED);
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
                shape.setOpacity(OPACITY_DESELECTED);
            }
            shape.setStroke(null);
        });
    }
}
