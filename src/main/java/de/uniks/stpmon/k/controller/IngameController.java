package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.interaction.DialogueController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

@Singleton
public class IngameController extends PortalController {

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
    InteractionStorage interactionStorage;

    @Inject
    TrainerStorage trainerStorage;

    @Inject
    WorldController worldController;

    @Inject
    InputHandler inputHandler;

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

        onDestroy(inputHandler.addPressedKeyFilter(event -> {
            if (mapOverview != null) {
                switch (event.getCode()) {
                    case A, D, W, S, LEFT, RIGHT, UP, DOWN, B -> {
                        // Block movement and backpack, if map overview is shown
                        if (mapOverview.isVisible()) {
                            event.consume();
                        }
                    }
                    case M -> {
                        if (mapOverview.isVisible()) {
                            mapOverview.setVisible(false);
                        } else {
                            mapOverview.setVisible(true);
                        }
                        event.consume();
                    }

                    case ESCAPE -> {
                        if (mapOverview.isVisible()) {
                            mapOverview.setVisible(false);
                            event.consume();
                        }
                    }

                }
            }
        }));
        starterController.init();
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

    public void addBackpackMenu(HBox backpackMenu) {
        ingameWrappingHBox.getChildren().add(0, backpackMenu);
    }

    public void removeBackpackMenu(HBox backpackMenu) {
        ingameWrappingHBox.getChildren().remove(backpackMenu);
    }

}
