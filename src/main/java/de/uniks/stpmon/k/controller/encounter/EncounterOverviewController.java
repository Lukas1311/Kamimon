package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.action.ActionFieldController;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.ClockService;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.service.world.WorldService;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.world.RouteData;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class EncounterOverviewController extends Controller {

    public static final double IMAGE_SCALE = 6.0;

    private final HashMap<EncounterSlot, Transition> attackAnimations = new HashMap<>(4);

    private final HashMap<EncounterSlot, Transition> changeAnimations = new HashMap<>(4);

    private final HashMap<EncounterSlot, ImageView> monsterImages = new HashMap<>(4);
    private final Map<EncounterSlot, String> slotMonsters = new HashMap<>(4);

    @FXML
    public StackPane fullBox;
    @FXML
    public ImageView background;
    @FXML
    public VBox userMonsters;
    @FXML
    public VBox opponentMonsters;
    @FXML
    public ImageView userMonster0;
    @FXML
    public ImageView userMonster1;
    @FXML
    public ImageView opponentMonster0;
    @FXML
    public ImageView opponentMonster1;
    @FXML
    public VBox actionFieldBox;

    private final Pane blackPane = new Pane();
    @FXML
    public VBox wrappingVBox;

    @Inject
    IResourceService resourceService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TextDeliveryService textDeliveryService;
    @Inject
    WorldService worldService;
    @Inject
    ClockService clockService;
    @Inject
    Provider<StatusController> statusControllerProvider;
    @Inject
    SessionService sessionService;
    @Inject
    ActionFieldController actionFieldController;

    @Inject
    public EncounterOverviewController() {
    }

    @Override
    public void init() {
        super.init();
        if (actionFieldController != null) {
            actionFieldController.init();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (actionFieldController != null) {
            actionFieldController.destroy();
        }
        slotMonsters.clear();
    }

    private TerrainType getEncounterTerrainType() {
        List<Property> properties = regionStorage.getArea().map().properties();

        if (properties == null) {
            return TerrainType.TOWN;
        }

        for (Property prop : properties) {
            if (prop.name().equals("Terrain")) {
                return switch (prop.value()) {
                    case "Lake" ->  TerrainType.LAKE;
                    case "Forest" -> TerrainType.FOREST;
                    case "Plains" -> TerrainType.PLAINS;
                    case "City" -> TerrainType.CITY;
                    case "Town" -> TerrainType.TOWN;
                    case "Coast" -> TerrainType.COAST;
                    case "Cave" -> TerrainType.CAVE;
                    default -> TerrainType.TOWN;
                };
            }
        }
        return TerrainType.TOWN;
    }

    private String getTerrainTypeName() {
        float nightFactor = worldService.getNightFactor(clockService.onTime().blockingFirst());
        TerrainType terrainType = getEncounterTerrainType();
        if (terrainType == TerrainType.CAVE) {
            return TerrainType.CAVE.name();
        } else {
            String timeOfDay = (nightFactor > 0) ? "NIGHT" : "DAY";
            return terrainType.name() + "_" + timeOfDay;
        }
    }

    private String getTerrainImagePath(List<RouteData> routeListData) {
        return getResourcePath() + "terrain/" + getTerrainTypeName() + ".png";
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        // relations between Encounter slots and image views
        monsterImages.put(EncounterSlot.PARTY_FIRST, userMonster0);
        monsterImages.put(EncounterSlot.PARTY_SECOND, userMonster1);
        monsterImages.put(EncounterSlot.ENEMY_FIRST, opponentMonster0);
        monsterImages.put(EncounterSlot.ENEMY_SECOND, opponentMonster1);

        Region currentRegion = regionStorage.getRegion();
        if (currentRegion.map() != null) {
            subscribe(
                textDeliveryService.getRouteData(currentRegion),
                data -> loadImage(background, getTerrainImagePath(data)) // load bg image dependant on area
            );
        }

        background.fitHeightProperty().bind(fullBox.heightProperty());
        background.fitWidthProperty().bind(fullBox.widthProperty());

        Parent actionField = this.actionFieldController.render();
        if (actionField != null) {
            actionFieldBox.getChildren().add(actionField);
        }

        //add a translation transition to all monster images
        for (EncounterSlot slot : monsterImages.keySet()) {
            ImageView view = monsterImages.get(slot);

            TranslateTransition translation =
                    new TranslateTransition(Duration.millis(effectContext.getEncounterAnimationSpeed() * 0.25), view);
            if (slot.enemy()) {
                translation.setByY(30);
                translation.setByX(-60);
            } else {
                translation.setByY(-30);
                translation.setByX(60);
            }
            translation.setAutoReverse(true);
            translation.setCycleCount(2);

            attackAnimations.put(slot, translation);
        }

        for (EncounterSlot slot : monsterImages.keySet()) {
            ImageView view = monsterImages.get(slot);

            TranslateTransition translation =
                    new TranslateTransition(Duration.millis(effectContext.getEncounterAnimationSpeed() * 0.35f), view);
            if (slot.enemy()) {
                translation.setByY(0);
                translation.setByX(1000);
            } else {
                translation.setByY(0);
                translation.setByX(-1000);
            }
            translation.setAutoReverse(true);
            translation.setCycleCount(2);

            changeAnimations.put(slot, translation);
        }

        if (effectContext.shouldSkipLoading()) {
            renderMonsterLists();
            animateMonsterEntrance();
            return parent;
        }

        subscribeFight();

        actionFieldBox.setOpacity(0);

        Transition openingTransition = playOpeningAnimation();

        if (openingTransition != null) {
            openingTransition.setOnFinished(event -> {
                fullBox.getChildren().remove(blackPane);
                renderMonsterLists();
                wrappingVBox.setOpacity(1.0);
                animateMonsterEntrance();
            });

            openingTransition.play();
        }

        return parent;
    }

    private Transition playOpeningAnimation() {
        if (fullBox.getChildren().contains(blackPane)) {
            return null;
        }
        blackPane.setPrefWidth(1280);
        blackPane.setPrefHeight(720);
        Rectangle rectangleTop = new Rectangle(1280, 360);
        rectangleTop.widthProperty().bind(fullBox.widthProperty());
        rectangleTop.heightProperty().bind(fullBox.heightProperty());
        Rectangle rectangleBottom = new Rectangle(1280, 360);
        rectangleBottom.widthProperty().bind(fullBox.widthProperty());
        rectangleBottom.heightProperty().bind(fullBox.heightProperty());
        rectangleBottom.setY(360);

        blackPane.getChildren().addAll(rectangleTop, rectangleBottom);

        fullBox.getChildren().add(blackPane);

        TranslateTransition rTopTransition = new TranslateTransition(Duration.seconds(1), rectangleTop);
        TranslateTransition rDownTransition = new TranslateTransition(Duration.seconds(1), rectangleBottom);

        rTopTransition.setToY(-800);
        rDownTransition.setToY(721);

        return new ParallelTransition(rTopTransition, rDownTransition);
    }

    private void subscribeFight() {
        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot), next -> {
                //using IMove to animate attack
                if (next.move() instanceof AbilityMove) {
                    renderAttack(slot);
                }
            });
        }
    }

    private void renderChange(EncounterSlot slot) {
        changeAnimations.get(slot).play();
    }


    private void renderAttack(EncounterSlot slot) {
        attackAnimations.get(slot).play();
    }

    private void renderMonsterLists() {
        for (EncounterSlot slot : sessionService.getSlots()) {
            if (slot.enemy()) {
                if (slot.partyIndex() == 0) {
                    renderMonsters(opponentMonsters, opponentMonster0, slot);
                } else {
                    renderMonsters(opponentMonsters, opponentMonster1, slot);
                }
            } else {
                if (slot.partyIndex() == 0) {
                    renderMonsters(userMonsters, userMonster0, slot);
                } else {
                    renderMonsters(userMonsters, userMonster1, slot);
                }
            }
        }
    }

    private void renderMonsters(VBox monstersContainer, ImageView monsterImageView, EncounterSlot slot) {
        StatusController statusController = statusControllerProvider.get();
        statusController.setSlot(slot);
        monstersContainer.getChildren().add(statusController.render());

        subscribe(sessionService.listenMonster(slot), (newMonster) -> {
                    if (newMonster == null) {
                        return;
                    }
                    String oldMonsterId = slotMonsters.get(slot);
                    if (oldMonsterId == null) {
                        loadMonsterImage(String.valueOf(newMonster.type()), monsterImageView, slot.enemy());
                    } else if (!oldMonsterId.equals(newMonster._id())) {
                        loadMonsterImage(String.valueOf(newMonster.type()), monsterImageView, slot.enemy());
                        renderChange(slot);
                    }
                    slotMonsters.put(slot, newMonster._id());
                }
        );

        if (slot.partyIndex() == 0) {
            if (!slot.enemy()) {
                VBox.setMargin(statusController.fullBox, new Insets(-18, 0, 0, 0));
            } else {
                VBox.setMargin(statusController.fullBox, new Insets(0, 125, 0, 0));
            }
        } else if (slot.partyIndex() == 1) {
            if (!slot.enemy()) {
                VBox.setMargin(statusController.fullBox, new Insets(-5, 0, 0, 125));
            }
        }
    }

    public void loadMonsterImage(String monsterId, ImageView monsterImage, boolean attacker) {
        subscribe(resourceService.getMonsterImage(monsterId), imageUrl -> {
            // Scale and set the image
            Image image = ImageUtils.scaledImageFX(imageUrl, IMAGE_SCALE);
            if (attacker) {
                monsterImage.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                monsterImage.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
            monsterImage.setImage(image);
        });
    }

    private void animateMonsterEntrance() {
        ObservableList<Node> teamMonsters = userMonsters.getChildren();
        if (teamMonsters.size() > 1) {
            teamMonsters.get(1).setOpacity(0);
            userMonster1.setOpacity(0);
        }
        ObservableList<Node> attackerMonsters = opponentMonsters.getChildren();
        if (attackerMonsters.size() > 1) {
            attackerMonsters.get(1).setOpacity(0);
            opponentMonster1.setOpacity(0);
        }

        //the first monster of the user and opponent always gets rendered
        ParallelTransition userFullTransition1 =
                createMonsterTransition(userMonster0, teamMonsters.get(0), false);
        ParallelTransition opponentFullTransition1 =
                createMonsterTransition(opponentMonster0, attackerMonsters.get(0), true);

        ParallelTransition parallel1 = new ParallelTransition(userFullTransition1, opponentFullTransition1);

        parallel1.setOnFinished(e -> {
            wrappingVBox.setOpacity(1);
            if (teamMonsters.size() > 1) {
                teamMonsters.get(1).setOpacity(1);
                userMonster1.setOpacity(1);
            }
            if (attackerMonsters.size() > 1) {
                attackerMonsters.get(1).setOpacity(1);
                opponentMonster1.setOpacity(1);
            }
        });

        ParallelTransition userFullTransition2 = null;
        if (teamMonsters.size() > 1) {
            userFullTransition2 = createMonsterTransition(userMonster1, teamMonsters.get(1), false);
        }

        ParallelTransition opponentFullTransition2 = null;
        if (attackerMonsters.size() > 1) {
            opponentFullTransition2 = createMonsterTransition(opponentMonster1, attackerMonsters.get(1), true);
        }

        ParallelTransition parallel2 = new ParallelTransition();
        if (userFullTransition2 != null) {
            parallel2.getChildren().add(userFullTransition2);
        }
        if (opponentFullTransition2 != null) {
            parallel2.getChildren().add(opponentFullTransition2);
        }
        SequentialTransition sequence = new SequentialTransition(parallel1, parallel2);
        sequence.setOnFinished(e -> actionFieldBox.setOpacity(1));

        TranslateTransition actionFieldTransition = new TranslateTransition(
                Duration.millis(effectContext.getEncounterAnimationSpeed()), actionFieldBox);
        actionFieldTransition.setFromX(600);
        actionFieldTransition.setToX(0);
        SequentialTransition fullSequence = new SequentialTransition(sequence, actionFieldTransition);

        fullSequence.play();
    }

    private ParallelTransition createMonsterTransition(Node image, Node status, boolean attacker) {
        //the first monster of the user and opponent always gets rendered
        return new ParallelTransition(createNodeTransition(status, attacker), createNodeTransition(image, attacker));
    }

    private TranslateTransition createNodeTransition(Node node, boolean fromRight) {
        TranslateTransition transition = new TranslateTransition(
                Duration.millis(effectContext.getEncounterAnimationSpeed()), node);
        transition.setFromX(fromRight ? 600 : -600);
        transition.setToX(0);
        return transition;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
