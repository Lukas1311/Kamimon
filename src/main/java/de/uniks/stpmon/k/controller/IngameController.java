package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.encounter.LoadingEncounterController;
import de.uniks.stpmon.k.controller.encounter.LoadingWildEncounterController;
import de.uniks.stpmon.k.controller.interaction.DialogueController;
import de.uniks.stpmon.k.controller.overworld.NightOverlayController;
import de.uniks.stpmon.k.controller.overworld.WorldTimerController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.AnimationService;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.animation.Transition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.input.InputEvent;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Stack;

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
    NightOverlayController nightOverlayController;
    @Inject
    Provider<EncounterOverviewController> encounterProvider;

    @Inject
    MonsterInformationController monsterInformationController;


    @Inject
    TrainerStorage trainerStorage;

    @Inject
    WorldController worldController;

    @Inject
    InputHandler inputHandler;

    @Inject
    SessionService encounterService;
    @Inject
    EncounterStorage encounterStorage;

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
        nightOverlayController.init();

        onDestroy(inputHandler.addPressedKeyFilter(event -> {
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
                Platform.runLater(() -> {
                    startEncounterAnimation(encounterStorage.getEncounter().isWild());
                });
            }));
        }
    }

    private void startEncounterAnimation(boolean isWild){
        //init
        StackPane overlayPane = new StackPane();
        overlayPane.setStyle("-fx-background-color: transparent");
        Circle blackpoint = new Circle(25.0);
        overlayPane.getChildren().add(blackpoint);
        ingameStack.getChildren().add(overlayPane);
        Transition transition = animationService.createEncounterAnimation(blackpoint);

        if(isWild){
            transition.setOnFinished(event -> {
                app.show(encounterWildProvider.get());
                ingameStack.getChildren().remove(overlayPane);
            });
        }else{
            transition.setOnFinished(event -> {
                app.show(loadingEncounterControllerProvider.get());
                ingameStack.getChildren().remove(overlayPane);
            });

        }

    }

    @Override
    public void destroy() {
        super.destroy();

        worldController.destroy();
        monsterBarController.destroy();
        minimapController.destroy();
        mapOverviewController.destroy();
        backpackController.destroy();
        dialogueController.destroy();
        starterController.destroy();
        worldTimerController.destroy();
        nightOverlayController.destroy();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        Parent world = this.worldController.render();
        // Null if unit testing world view
        if (world != null) {
            ingameStack.getChildren().add(0, world);
        }
        Parent monsterBar = this.monsterBarController.render();
        // Null if unit testing world view
        if (monsterBar != null) {
            pane.getChildren().add(monsterBar);
        }

        Parent miniMap = this.minimapController.render();
        // Null if unit testing world view
        if (miniMap != null) {
            rightVbox.getChildren().add(0, miniMap);
        }

        Parent worldTimer = this.worldTimerController.render();
        if (worldTimer != null) {
            rightVbox.getChildren().add(0, worldTimer);
        }

        Parent nightOverlay = this.nightOverlayController.render();
        if (nightOverlay != null) {
            ingameStack.getChildren().add(1, nightOverlay);
        }

        Parent backPack = this.backpackController.render();
        // Null if unit testing world view
        if (backPack != null) {
            ingameWrappingHBox.getChildren().add(backPack);
            ingameStack.setAlignment(Pos.TOP_RIGHT);
        }

        Parent dialogue = this.dialogueController.render();
        if (dialogue != null) {
            dialogueBox.getChildren().clear();
            dialogueBox.getChildren().add(dialogue);
            dialogue.setVisible(false);
        }

        if (miniMap != null) {
            miniMap.setOnMouseClicked(this::openOrCloseMap);
        }

        Parent starter = this.starterController.render();
        if (starter != null) {
            starterBox.getChildren().clear();
            starterBox.getChildren().add(starter);
            starter.setVisible(false);
        }

        return parent;
    }

    private void openOrCloseMap(InputEvent event) {
        if (mapOverview == null) {
            openMap();
        } else {
            closeMap();
        }
        event.consume();
    }

    public void openMap() {
        if (mapOverviewController == null) {
            return;
        }
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

        timeline.setOnFinished(event -> ingameStack.getChildren().remove(overlayPane));
    }
}
