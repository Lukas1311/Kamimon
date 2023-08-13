package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.action.ActionFieldController;
import de.uniks.stpmon.k.controller.inventory.InventoryController;
import de.uniks.stpmon.k.controller.inventory.ItemInformationController;
import de.uniks.stpmon.k.controller.monsters.MonsterInformationController;
import de.uniks.stpmon.k.controller.monsters.MonsterSelectionController;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.service.AnimationService;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.SoundService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.ClockService;
import de.uniks.stpmon.k.service.world.WorldService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class EncounterOverviewController extends Controller {
    public static final double IMAGE_SCALE_ATTACKER = 6.0;
    public static final double IMAGE_SCALE_OPPONENT = 4.0;

    private final HashMap<EncounterSlot, Transition> attackAnimations = new HashMap<>(4);
    private final HashMap<EncounterSlot, Transition> changeAnimations = new HashMap<>(4);
    private final HashMap<EncounterSlot, ImageView> monsterImages = new HashMap<>(4);
    private final Map<EncounterSlot, Monster> slotMonsters = new HashMap<>(4);

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
    public VBox actionFieldWrapperBox;
    @FXML
    public VBox wrappingVBox;
    @FXML
    public HBox contentBox;
    @FXML
    public VBox menuHolderVBox;
    @FXML
    public ImageView ballView;

    @Inject
    IResourceService resourceService;
    @Inject
    RegionStorage regionStorage;
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
    Provider<MonsterInformationController> monInfoProvider;
    @Inject
    Provider<InventoryController> inventoryControllerProvider;
    @Inject
    Provider<ItemInformationController> itemInformationControllerProvider;

    @Inject
    Provider<MonsterSelectionController> monsterSelectionControllerProvider;
    @Inject
    SoundService soundService;
    @Inject
    AnimationService animationService;

    private final Pane blackPane = new Pane();
    public Parent controller;
    public Parent monInfoParent;
    public Item item;
    private boolean caught = false;

    @Inject
    public EncounterOverviewController() {
    }

    @Override
    public void init() {
        super.init();
        if (actionFieldController != null) {
            actionFieldController.init();
            soundService.loopSong("13_Trainer_Battle");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (actionFieldController != null) {
            actionFieldController.destroy();
        }
        if (soundService != null) {
            soundService.destroy();
        }
        inventoryControllerProvider.get().setInEncounter(false);
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
                    case "Lake" -> TerrainType.LAKE;
                    case "Forest" -> TerrainType.FOREST;
                    case "Plains" -> TerrainType.PLAINS;
                    case "City" -> TerrainType.CITY;
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

    private String getTerrainImagePath() {
        return getResourcePath() + "terrain/" + getTerrainTypeName() + ".png";
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        contentBox.setPickOnBounds(false);
        actionFieldWrapperBox.setPickOnBounds(false);
        menuHolderVBox.setPickOnBounds(false);

        // relations between Encounter slots and image views
        monsterImages.put(EncounterSlot.PARTY_FIRST, userMonster0);
        monsterImages.put(EncounterSlot.PARTY_SECOND, userMonster1);
        monsterImages.put(EncounterSlot.ENEMY_FIRST, opponentMonster0);
        monsterImages.put(EncounterSlot.ENEMY_SECOND, opponentMonster1);

        Region currentRegion = regionStorage.getRegion();
        if (currentRegion.map() != null) {
            loadImage(background, getTerrainImagePath());
        }

        background.fitHeightProperty().bind(fullBox.heightProperty());
        background.fitWidthProperty().bind(fullBox.widthProperty());

        Parent actionField = this.actionFieldController.render();
        if (actionField != null) {
            actionFieldWrapperBox.getChildren().add(actionField);
            actionFieldWrapperBox.setSpacing(5);

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

        animationService.setUserMonster0(userMonster0);
        animationService.setUserMonster1(userMonster1);
        animationService.setOpponentMonster0(opponentMonster0);
        animationService.setOpponentMonster1(opponentMonster1);
        animationService.setActionFieldWrapperBox(actionFieldWrapperBox);
        animationService.setBallView(ballView);

        if (effectContext.shouldSkipLoading()) {
            renderMonsterLists();
            animationService.animateMonsterEntrance(userMonsters, opponentMonsters, wrappingVBox);
            return parent;
        }

        actionFieldWrapperBox.setOpacity(0);

        Transition openingTransition = animationService.playOpeningAnimation(fullBox, blackPane);

        if (openingTransition != null) {
            openingTransition.setOnFinished(event -> {
                fullBox.getChildren().remove(blackPane);
                renderMonsterLists();
                wrappingVBox.setOpacity(1.0);
                animationService.animateMonsterEntrance(userMonsters, opponentMonsters, wrappingVBox);
            });
            openingTransition.play();
        }
        return parent;
    }

    public void showLevelUp(Monster oldMon, Monster newMon) {
        if (controller == null) {
            MonsterInformationController monInfoController = monInfoProvider.get();
            controller = monInfoController.render();
            monInfoController.loadLevelUp(oldMon, newMon);
            contentBox.getChildren().add(0, controller);
        }
    }

    public void showMonInfo(Monster mon) {
        if (monInfoParent == null) {
            MonsterInformationController monInfoController = monInfoProvider.get();
            this.monInfoParent = monInfoController.render();
            monInfoController.loadMonsterTypeDto(String.valueOf(mon.type()));
            monInfoController.loadMonster(mon);
            contentBox.getChildren().add(0, this.monInfoParent);
        }
    }

    public void removeMonInfo() {
        contentBox.getChildren().remove(0);
        monInfoProvider.get().destroy();
        this.monInfoParent = null;
    }

    public void openController(String child, Item item) {
        if (controller == null) {
            if (child.equals("inventory")) {
                InventoryController inventoryController = inventoryControllerProvider.get();
                controller = inventoryController.render();
            } else {
                return;
            }
        } else {
            if (child.equals("itemInfo")) {
                while (contentBox.getChildren().size() > 1) {
                    contentBox.getChildren().remove(0);
                }
                ItemInformationController itemInformationController = itemInformationControllerProvider.get();
                itemInformationController.setInEncounter(true);
                itemInformationController.setItem(item);
                controller = itemInformationController.render();
            } else if (child.equals("monsterSelection")) {
                while (contentBox.getChildren().size() > 2) {
                    contentBox.getChildren().remove(0);
                }
                MonsterSelectionController monsterSelectionController = monsterSelectionControllerProvider.get();
                monsterSelectionController.setItem(item.type());
                controller = monsterSelectionController.render();
            }
        }
        contentBox.getChildren().add(0, controller);
    }

    public void removeController(String child) {
        if (controller != null) {
            contentBox.getChildren().clear();
            switch (child) {
                case "inventory" -> {
                    inventoryControllerProvider.get().destroy();
                    itemInformationControllerProvider.get().destroy();
                    monsterSelectionControllerProvider.get().destroy();
                }
                case "itemInfo" -> {
                    itemInformationControllerProvider.get().destroy();
                    monsterSelectionControllerProvider.get().destroy();
                }
                case "monsterSelection" -> monsterSelectionControllerProvider.get().destroy();
                case "monInfo" -> monInfoProvider.get().destroy();
                case "all" -> {
                    inventoryControllerProvider.get().destroy();
                    itemInformationControllerProvider.get().destroy();
                    monsterSelectionControllerProvider.get().destroy();
                    monInfoProvider.get().destroy();
                }
                default -> {
                    return;
                }
            }
            controller = null;
        }
    }

    private void renderChange(EncounterSlot slot) {
        monsterImages.get(slot).setOpacity(1.0f);
        changeAnimations.get(slot).play();
    }

    public void renderAttack(EncounterSlot slot) {
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
        int index = monstersContainer.getChildren().size() - 1;
        subscribe(sessionService.listenOpponentDeletion(slot),
                (deleted) -> monsterImageView.setOpacity(0.0f));
        subscribe(sessionService.listenMonster(slot), (newMonster) -> {
                    if (newMonster == null) {
                        return;
                    }
            Monster newMonsterId = slotMonsters.get(slot);
            if (newMonsterId == null) {
                        loadMonsterImage(String.valueOf(newMonster.type()), monsterImageView, slot.enemy());
            } else if (!newMonsterId._id().equals(newMonster._id())) {
                        loadMonsterImage(String.valueOf(newMonster.type()), monsterImageView, slot.enemy());
                        renderChange(slot);
                // Rerender status if opponent joins, needed to change status background
                if (!Objects.equals(newMonster.trainer(), newMonsterId.trainer())) {
                    // Destroy the old status controller and create a new one
                    statusController.destroy();
                    monstersContainer.getChildren().set(index, statusController.render());
                }
                    }
            slotMonsters.put(slot, newMonster);
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
            Image image;
            if (attacker) {
                image = ImageUtils.scaledImageFX(imageUrl, IMAGE_SCALE_ATTACKER);
                monsterImage.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                image = ImageUtils.scaledImageFX(imageUrl, IMAGE_SCALE_OPPONENT);
                monsterImage.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
            monsterImage.setImage(image);
        });
    }

    public void monBallAnimation(int item) {
        if (item == 0) {
            return;
        }
        subscribe(resourceService.getItemImage(String.valueOf(item)), item1 -> {
            //image
            Image ball = ImageUtils.scaledImageFX(item1, 3.0);
            ballView.setImage(ball);
        });
        if (ballView.getOpacity() == 0) {
            ballView.setOpacity(1.0f);
        }
        //transition for MonBall
        SequentialTransition sequentialTransition = animationService.getBallFlight();

        SequentialTransition sequentialRotation = new SequentialTransition(); // To control rotation sequence

        int totalRotations = 3; // Total number of rotations
        for (int i = 0; i < totalRotations; i++) {
            RotateTransition rotateBallTransition = animationService.getBallRotation(i);

            sequentialRotation.getChildren().add(rotateBallTransition);
        }
        sequentialRotation.setCycleCount(3);

        SequentialTransition finalAnimation = new SequentialTransition(sequentialTransition, sequentialRotation);
        finalAnimation.play();
        finalAnimation.setOnFinished(event -> {
            if (!successfullyCaught()) {
                ParallelTransition revertAnimation = animationService.revertCatchAnimation();
                revertAnimation.play();
            }
            setCaught(false);
        });
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }

    public boolean successfullyCaught() {
        return caught;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
