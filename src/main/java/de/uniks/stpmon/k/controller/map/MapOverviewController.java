package de.uniks.stpmon.k.controller.map;

import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.ToastedController;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.world.RouteData;
import javafx.event.Event;
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
import javafx.scene.transform.Rotate;

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
    TrainerService trainerService;
    @Inject
    RegionService regionService;
    @Inject
    TextDeliveryService textDeliveryService;
    @Inject
    WorldRepository worldRepository;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    TeleportAnimation teleportAnimation;
    @Inject
    InputHandler inputHandler;

    private Shape activeShape;
    private Region currentRegion;
    private String currentAreaId;
    private Trainer currentTrainer;
    private String currentPosition;
    private Set<String> visitedAreaIds = new HashSet<>();
    private Polygon playerDart;
    private Rotate playerRotate;


    @Inject
    public MapOverviewController() {
    }

    @Override
    public void init() {
        super.init();
        currentRegion = regionStorage.getRegion();
        currentTrainer = trainerStorage.getTrainer();
        visitedAreaIds = currentTrainer.visitedAreas();
        currentAreaId = currentTrainer.area();
        playerDart = new Polygon();
        playerDart.getPoints().addAll(
                -50.0, 40.0,
                50.0, 40.0,
                0.0, -60.0
        );
        playerRotate = new Rotate(0, 0, 0);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        fastTravelButton.setText(translateString("fastTravel"));
        fastTravelButton.setVisible(false);

        // center the region name label horizontally
        AnchorPane.setLeftAnchor(regionNameLabel, 0.0);
        AnchorPane.setRightAnchor(regionNameLabel, 0.0);

        loadBgImage(mapOverviewHolder, getResourcePath() + "mapOverview_v2.png");

        regionNameLabel.setText(currentRegion.name());
        if (currentRegion.map() != null) {
            subscribe(
                    worldRepository.regionMap().onValue(),
                    renderedMap -> {
                        if (renderedMap.isEmpty()) {
                            return;
                        }
                        Image map = ImageUtils.toFXImage(renderedMap.get());
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
                            areas -> renderMapDetails(data, filterVisitedAreas(areas), currentPosition)
                    )
            );
        }

        setupPlayerDart();

        return parent;
    }

    private void fastTravel(String area) {
        // close the map before because it would be somehow still open after travel
        ingameControllerProvider.get().closeMap();
        onDestroy(inputHandler.addReleasedKeyFilter(Event::consume));
        onDestroy(inputHandler.addPressedKeyFilter(Event::consume));
        teleportAnimation.playFastTravelAnimation(
                () -> subscribe(trainerService.fastTravel(area))
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
        playerDart = null;
    }

    private HashMap<String, AreaInfo> filterVisitedAreas(List<Area> areas) {
        HashMap<String, AreaInfo> visitedAreas = new HashMap<>();
        for (Area area : areas) {
            if (area._id().equals(currentAreaId)) {
                currentPosition = area.name();
            }
            boolean hasSpawn = false;
            if (!visitedAreaIds.contains(area._id())) {
                continue;
            }
            if (area.map() == null) {
                continue;
            }
            List<Property> properties = area.map().properties();
            if (properties != null) {
                for (Property prop : properties) {
                    if (prop.name().equals("Spawn")) {
                        hasSpawn = true;
                        break;
                    }
                }
            }
            visitedAreas.put(area.name(), new AreaInfo(area, hasSpawn));
        }
        return visitedAreas;
    }

    private void renderMapDetails(List<RouteData> routeListData, HashMap<String, AreaInfo> visitedAreas, String currentPosition) {

        double originalHeight = mapImageView.getImage().getHeight();
        double scaledHeight = mapImageView.getFitHeight();
        double scaledWidth = mapImageView.getLayoutBounds().getWidth();
        double scaleRatio = scaledHeight / originalHeight;

        double offsetX = (mapStackPane.getWidth() - scaledWidth) / 2.0;


        routeListData.forEach(routeData -> {
            boolean visited = visitedAreas.containsKey(routeData.routeText().name());
            AreaInfo info = null;
            boolean isCurrentPos = false;
            if (visited) {

                if (routeData.routeText().name().equals(currentPosition)) {
                    isCurrentPos = true;
                }

                info = visitedAreas.get(routeData.routeText().name());
            }

            if (!routeData.polygon().isEmpty()) {
                Polygon polygon = new Polygon();
                for (PolygonPoint point : routeData.polygon()) {
                    polygon.getPoints().addAll(
                            (double) (routeData.x() + point.x()) * scaleRatio + offsetX,
                            (double) (routeData.y() + point.y()) * scaleRatio
                    );
                }

                if (isCurrentPos) {
                    double posX = routeData.x() * scaleRatio + offsetX + (polygon.getBoundsInLocal().getWidth() * scaleRatio / 2.0);
                    double posY = routeData.y() * scaleRatio + (polygon.getBoundsInLocal().getHeight() * scaleRatio / 2.0);
                    setPlayerDartPosition(posX, posY);
                }

                addDetailShape(polygon, routeData, visited, info);
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

            if (isCurrentPos) {
                double posX = routeData.x() * scaleRatio + offsetX + (routeData.width() / 2.0 * scaleRatio);
                double posY = routeData.y() * scaleRatio + (routeData.height() / 2.0 * scaleRatio);
                setPlayerDartPosition(posX, posY);
            }

            addDetailShape(rectangle, routeData, visited, info);
        });
    }

    private void setupPlayerDart() {
        playerDart.setScaleX(0.2);
        playerDart.setScaleY(0.2);
        playerDart.setFill(Color.WHITE);
        playerDart.setStroke(Color.web("#706880"));
        playerDart.setStrokeWidth(10);
        playerDart.setMouseTransparent(true);
        mapStackPane.getChildren().add(playerDart);
        playerDart.toFront();

        int direction = currentTrainer.direction();
        int newRotation = PlayerDirection.values()[direction].getDegrees();


        if (newRotation != playerRotate.getAngle()) {
            playerRotate.setAngle(newRotation);
            playerDart.getTransforms().add(playerRotate);
        }
    }

    private void setPlayerDartPosition(double posX, double posY) {
        double dartHeight = playerDart.getBoundsInLocal().getHeight() * playerDart.getScaleY();

        playerDart.setTranslateX(posX - playerDart.getLayoutX());
        playerDart.setTranslateY(posY - playerDart.getLayoutY() + dartHeight / 2);
    }

    private void addDetailShape(Shape shape, RouteData routeData, boolean isVisited, AreaInfo areaInfo) {
        shape.setId("detail_" + routeData.id());
        // set the area id as hidden user data
        shape.setUserData(areaInfo != null ? areaInfo.area()._id() : null);

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

            if (isVisited && areaInfo != null) {
                areaNameLabel.setText(routeData.routeText().name());
                String desc = routeData.routeText().description();
                String buildings = routeData.buildings();
                if (!buildings.isEmpty()) {
                    desc += "\n" + translateString("map.buildings", buildings.replace("\n", ", "));
                }
                if (desc.isEmpty()) {
                    desc = "Here could be your advertisement.";
                }
                regionDescription.setText(desc);
                fastTravelButton.setVisible(areaInfo.hasSpawn);
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

    @Override
    public String getResourcePath() {
        return "map/";
    }

    private record AreaInfo(Area area, boolean hasSpawn) {
    }
}
