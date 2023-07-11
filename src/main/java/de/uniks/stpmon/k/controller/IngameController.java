package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.interaction.DialogueController;
import de.uniks.stpmon.k.controller.overworld.WorldTimerController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Stack;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

@Singleton
public class IngameController extends PortalController {
    //TODO: Remove if encounter leave is implemented
    public static boolean disableEncounter = false;
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
    Provider<EncounterOverviewController> encounterProvider;
    @Inject
    WorldTimerController worldTimerController;
    @Inject
    InteractionStorage interactionStorage;
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

        onDestroy(inputHandler.addPressedKeyFilter(event -> {
            if (mapOverview != null) {
                switch (event.getCode()) {
                    case A, D, W, S, LEFT, RIGHT, UP, DOWN, B, E-> {
                        // Block movement and backpack, if map overview is shown
                        if (mapOverview.isVisible()) {
                            event.consume();
                        }
                    }
                    case M -> {
                        mapOverview.setVisible(!mapOverview.isVisible());
                        event.consume();
                    }

                    case ESCAPE -> {
                        if (mapOverview.isVisible()) {
                            mapOverview.setVisible(false);
                            event.consume();
                        }
                    }

                    default -> {
                    }

                }
            }
        }));
        starterController.init();

        if (encounterService != null) {
            if (disableEncounter) {
                return;
            }
            subscribe(encounterService.tryLoadEncounter(), () -> {
                if (encounterService.hasNoEncounter()) {
                    return;
                }
                EncounterOverviewController controller = encounterProvider.get();
                app.show(controller);
            });
            subscribe(encounterService.listenForEncounter()
                    .subscribeOn(Schedulers.computation()), () -> {
                if (encounterService.hasNoEncounter()) {
                    return;
                }
                EncounterOverviewController controller = encounterProvider.get();
                app.show(controller);
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

        mapOverview = this.mapOverviewController.render();
        Parent backPack = this.backpackController.render();
        // Null if unit testing world view
        if (backPack != null) {
            ingameWrappingHBox.getChildren().add(backPack);
            ingameStack.setAlignment(Pos.TOP_RIGHT);
        }

        if (mapOverview != null) {
            ingameStack.getChildren().add(mapOverview);
            ingameStack.setAlignment(Pos.CENTER);
            mapOverview.setVisible(false);
        }


        Parent dialogue = this.dialogueController.render();
        if (dialogue != null) {
            dialogueBox.getChildren().clear();
            dialogueBox.getChildren().add(dialogue);
            dialogue.setVisible(false);
        }

        if (miniMap != null && mapOverview != null) {
            miniMap.setOnMouseClicked(click -> mapOverview.setVisible(true));
        }

        Parent starter = this.starterController.render();
        if (starter != null) {
            starterBox.getChildren().clear();
            starterBox.getChildren().add(starter);
            starter.setVisible(false);
        }

        return parent;
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
}
