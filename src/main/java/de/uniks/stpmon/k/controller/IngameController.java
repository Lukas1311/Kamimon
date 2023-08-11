package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.backpack.BackpackController;
import de.uniks.stpmon.k.controller.backpack.BackpackMenuController;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.encounter.LoadingEncounterController;
import de.uniks.stpmon.k.controller.encounter.LoadingWildEncounterController;
import de.uniks.stpmon.k.controller.interaction.DialogueController;
import de.uniks.stpmon.k.controller.inventory.ItemInformationController;
import de.uniks.stpmon.k.controller.map.MapOverviewController;
import de.uniks.stpmon.k.controller.map.MinimapController;
import de.uniks.stpmon.k.controller.mondex.MonDexDetailController;
import de.uniks.stpmon.k.controller.monsters.MonsterBarController;
import de.uniks.stpmon.k.controller.monsters.MonsterInformationController;
import de.uniks.stpmon.k.controller.monsters.MonsterInventoryController;
import de.uniks.stpmon.k.controller.overworld.WorldTimerController;
import de.uniks.stpmon.k.controller.shop.ShopOptionController;
import de.uniks.stpmon.k.controller.shop.ShopOverviewController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.AnimationService;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.SoundService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import io.reactivex.rxjava3.annotations.Nullable;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Stack;
import java.util.function.BiConsumer;

import static de.uniks.stpmon.k.controller.StarterController.StarterOption.ITEM;
import static de.uniks.stpmon.k.controller.StarterController.StarterOption.MON;
import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

@Singleton
public class IngameController extends PortalController {

    private final Stack<Controller> tabStack = new Stack<>();

    @FXML
    public StackPane ingameStack;
    @FXML
    public BorderPane ingame;
    @FXML
    public Pane pane;
    @FXML
    public VBox rightVbox;
    @FXML
    public HBox ingameWrappingHBox;
    @FXML
    public HBox dialogueBox;
    @FXML
    public VBox starterBox;
    @FXML
    public BorderPane mainPain;
    @FXML
    public BorderPane rightMenuBorderPane;
    @FXML
    public VBox miniMapVBox;
    @FXML
    public BorderPane shopBorderPane;
    @FXML
    public HBox shopHBox;

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    MapOverviewController mapOverviewController;
    @Inject
    MonsterBarController monsterBarController;
    @Inject
    MinimapController minimapController;
    @Inject
    BackpackController backpackController;
    @Inject
    DialogueController dialogueController;
    @Inject
    StarterController starterController;
    @Inject
    Provider<LoadingEncounterController> loadingEncounterControllerProvider;
    @Inject
    Provider<LoadingWildEncounterController> encounterWildProvider;
    @Inject
    AnimationService animationService;
    @Inject
    WorldTimerController worldTimerController;
    @Inject
    MonsterInformationController monsterInformationController;
    @Inject
    MonsterInventoryController monsterInventoryController;
    @Inject
    MonDexDetailController monDexDetailController;
    @Inject
    ItemInformationController itemInformationController;
    @Inject
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;
    @Inject
    Provider<BackpackMenuController> backpackMenuControllerProvider;
    @Inject
    WorldController worldController;
    @Inject
    ShopOverviewController shopOverviewController;
    @Inject
    ShopOptionController shopOptionController;
    @Inject
    InputHandler inputHandler;
    @Inject
    SessionService encounterService;
    @Inject
    EncounterStorage encounterStorage;
    @Inject
    SoundService soundService;

    private Parent mapOverview;

    @Inject
    public IngameController() {
    }

    @Override
    public void init() {
        super.init();

        worldController.init();
        monsterBarController.init();
        minimapController.init();
        mapOverviewController.init();
        backpackController.init();
        dialogueController.init();
        worldTimerController.init();
        soundService.init();

        onDestroy(inputHandler.addPressedKeyFilter(event -> {
            // Block user input if he is in an encounter
            if (!encounterStorage.isEmpty()) {
                event.consume();
                return;
            }
            switch (event.getCode()) {
                case A, D, W, S, LEFT, RIGHT, UP, DOWN, B, E -> {
                    // Block movement and backpack, if map overview is shown
                    if (mapOverview != null) {
                        event.consume();
                    }
                }
                case M -> openOrCloseMap(event);

                case ESCAPE -> {
                    if (mapOverview != null) {
                        closeMap();
                        event.consume();
                    }
                }

                default -> {
                }

            }
        }));
        // Block user input if he is in an encounter
        onDestroy(inputHandler.addReleasedKeyFilter(event -> {
            if (!encounterStorage.isEmpty()) {
                event.consume();
            }
        }));
        starterController.init();

        if (encounterService != null) {
            subscribe(encounterService.tryLoadEncounter(), () -> {
                if (encounterService.hasNoEncounter()) {
                    return;
                }
                startEncounterAnimation(encounterStorage.getEncounter().isWild());
            });
            disposables.add(encounterService.listenForEncounter().subscribe(() -> {
                if (encounterService.hasNoEncounter()) {
                    return;
                }
                Platform.runLater(() -> startEncounterAnimation(encounterStorage.getEncounter().isWild()));
            }));
        }
    }

    private void startEncounterAnimation(boolean isWild) {
        if (effectContext.shouldSkipLoading()) {
            EncounterOverviewController controller = encounterOverviewControllerProvider.get();
            app.show(controller);
            return;
        }

        //init
        StackPane overlayPane = new StackPane();
        overlayPane.setStyle("-fx-background-color: transparent");
        Circle blackpoint = new Circle(25.0);
        overlayPane.getChildren().add(blackpoint);
        ingameStack.getChildren().add(overlayPane);

        Transition transition = animationService.createEncounterAnimation(blackpoint);

        if (isWild) {
            transition.setOnFinished(event -> {
                ingameStack.getChildren().remove(overlayPane);
                app.show(encounterWildProvider.get());

            });
        } else {
            transition.setOnFinished(event -> {
                ingameStack.getChildren().remove(overlayPane);
                app.show(loadingEncounterControllerProvider.get());
            });
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        removeChildren(0);
        worldController.destroy();
        monsterBarController.destroy();
        minimapController.destroy();
        mapOverviewController.destroy();
        backpackController.destroy();
        dialogueController.destroy();
        starterController.destroy();
        worldTimerController.destroy();
        shopOptionController.destroy();
        shopOverviewController.destroy();
        soundService.destroy();
        ingameStack.getChildren().clear();
        ingame.getChildren().clear();
        ingameWrappingHBox.getChildren().clear();
        rightVbox.getChildren().clear();
        dialogueBox.getChildren().clear();
        starterBox.getChildren().clear();
        mainPain.getChildren().clear();
        rightMenuBorderPane.getChildren().clear();
        miniMapVBox.getChildren().clear();
        ingameStack = null;
        ingame = null;
        ingameWrappingHBox = null;
        rightVbox = null;
        dialogueBox = null;
        starterBox = null;
        mainPain = null;
        rightMenuBorderPane = null;
        miniMapVBox = null;
        pane = null;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        rightMenuBorderPane.setPickOnBounds(false);
        ingameWrappingHBox.setPickOnBounds(false);
        shopHBox.setPickOnBounds(false);
        shopBorderPane.setPickOnBounds(false);

        renderAndInsert(ingameStack, worldController);
        renderAndInsert(pane, monsterBarController);
        renderAndInsert(miniMapVBox, minimapController,
                (_parent, child) -> child.setOnMouseClicked(this::openOrCloseMap));
        renderAndInsert(miniMapVBox, worldTimerController);

        renderAndInsert(ingameWrappingHBox, backpackController,
                (_parent, _child) -> ingameStack.setAlignment(Pos.TOP_RIGHT));

        dialogueBox.getChildren().clear();
        renderAndInsert(dialogueBox, dialogueController,
                (_parent, child) -> child.setVisible(false));

        starterBox.getChildren().clear();
        renderAndInsert(starterBox, starterController,
                (_parent, child) -> child.setVisible(false));

        return parent;
    }

    private void renderAndInsert(Pane parent, Controller controller) {
        renderAndInsert(parent, controller, null);
    }

    private void renderAndInsert(Pane parent, Controller controller,
                                 @Nullable BiConsumer<Parent, Parent> setup) {
        if (controller == null) {
            return;
        }
        Parent child = controller.render();
        if (child == null || parent == null) {
            return;
        }
        parent.getChildren().add(0, child);
        if (setup != null) {
            setup.accept(parent, child);
        }
    }

    private void openOrCloseMap(InputEvent event) {
        openOrCloseMap();
        event.consume();
    }

    public void openOrCloseMap() {
        if (mapOverview == null) {
            openMap();
            backpackMenuControllerProvider.get().closeAll();
        } else {
            closeMap();
        }
    }

    public boolean isMapOpen() {
        return mapOverview != null;
    }

    public void openMap() {
        if (mapOverviewController == null) {
            return;
        }

        mapOverviewController.init();
        mapOverview = this.mapOverviewController.render();
        ingameStack.getChildren().add(mapOverview);
        mainPain.setOnMouseClicked(click -> {
            closeMap();
            click.consume();
        });
        ingameStack.setAlignment(Pos.CENTER);
    }

    public void closeMap() {
        if (mapOverview == null) {
            return;
        }
        mapOverviewController.destroy();
        ingameStack.getChildren().remove(mapOverview);
        mainPain.setOnMouseClicked(null);
        mapOverview = null;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }

    public void pushController(Controller controller) {
        ObservableList<Node> children = ingameWrappingHBox.getChildren();

        controller.init();
        tabStack.push(controller);
        children.add(0, controller.render());
    }

    public void removeChildren(int endIndex) {
        for (int i = tabStack.size() - 1; i >= endIndex; i--) {
            ObservableList<Node> children = ingameWrappingHBox.getChildren();
            Controller controller = tabStack.pop();
            controller.destroy();
            children.remove(0);
        }
    }

    public void closeMonsterInfo() {
        removeChildren(2);
    }

    public void openMonsterInfo(Monster monster) {
        ObservableList<Node> children = ingameWrappingHBox.getChildren();

        MonsterInformationController controller = monsterInformationController;
        controller.init();
        tabStack.push(controller);

        Parent monsterInfo = controller.render();
        controller.loadMonsterTypeDto(String.valueOf(monster.type()));
        controller.loadMonster(monster);
        children.add(0, monsterInfo);
    }

    public void openMonDexDetail(MonsterTypeDto mon) {
        ObservableList<Node> children = ingameWrappingHBox.getChildren();

        MonDexDetailController controller = monDexDetailController;
        controller.init();
        tabStack.push(controller);

        Parent monsterInfo = controller.render();
        controller.loadMon(mon);
        children.add(0, monsterInfo);
    }

    public void openItemInformation(Item item) {
        ObservableList<Node> children = ingameWrappingHBox.getChildren();

        ItemInformationController controller = itemInformationController;
        controller.setInEncounter(false);
        controller.init();
        tabStack.push(controller);

        controller.setItem(item);

        Parent itemInfo = controller.render();
        children.add(0, itemInfo);
    }

    public <T> void openBox(T value) {
        ObservableList<Node> children = ingameWrappingHBox.getChildren();

        itemInformationController.setOpen(true);

        StarterController controller = starterController;
        if (value instanceof Item item) {
            controller.setStarter(item.type().toString(), ITEM);
        } else if (value instanceof Monster monster) {
            controller.setStarter(monster.type().toString(), MON);
        }
        controller.init();
        tabStack.push(controller);

        Parent starter = controller.render();
        children.add(0, starter);
    }

    public void openMonsterInventory() {
        ObservableList<Node> children = ingameWrappingHBox.getChildren();

        monsterInventoryController.setSelectionMode(true);
        tabStack.push(monsterInventoryController);

        Parent monsterInventory = monsterInventoryController.render();
        children.add(0, monsterInventory);
    }


    public void applyHealEffect() {
        Pane overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5);");

        ingameStack.getChildren().add(overlayPane);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(overlayPane.opacityProperty(), 0.0)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(overlayPane.opacityProperty(), 0.5)),
                new KeyFrame(Duration.seconds(1), new KeyValue(overlayPane.opacityProperty(), 0.0)));

        timeline.setCycleCount(3);
        timeline.setAutoReverse(true);
        timeline.play();

        timeline.setOnFinished(event -> {
            if (ingameStack == null) {
                return;
            }
            ingameStack.getChildren().remove(overlayPane);
        });
    }

    /**
     * Opens the shop user interface.
     *
     * @param npc the trainer that is the shop owner
     */
    public void openShop(Trainer npc) {
        shopOverviewController.init();
        Parent shopList = this.shopOverviewController.render();
        if (shopList != null) {
            shopHBox.getChildren().add(shopList);
        }

        shopOptionController.init();
        Parent shopDetail = this.shopOptionController.render();
        if (shopDetail != null) {
            shopHBox.getChildren().add(shopDetail);
        }

        shopOptionController.setTrainer(npc);
        shopOverviewController.setTrainer(npc);
        shopOverviewController.initSelection();

    }

    /**
     * Closes the shop user interface.
     */
    public void closeShop() {
        shopHBox.getChildren().clear();
        shopOptionController.destroy();
        shopOverviewController.destroy();
    }

}
