package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.UiToggle;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

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
    public Text inGameText;
    @FXML
    public VBox rightVbox;
    @FXML
    public HBox ingameWrappingHBox;

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
    TrainerStorage trainerStorage;

    @Inject
    protected WorldController worldController;



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
    }

    @Override
    public void destroy() {
        super.destroy();

        worldController.destroy();
        monsterBarController.destroy();
        minimapController.destroy();
        mapOverviewController.destroy();
        backpackController.destroy();
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

        Parent mapOverview = this.mapOverviewController.render();
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

        UiToggle mapToggle = new UiToggle(false);
        miniMap.setOnMouseClicked(click -> {
            // TODO: block inputs while big map is open? (e.g. walking?)
            boolean isMapVisible = mapToggle.toggle();
            mapOverview.setVisible(isMapVisible);
            System.out.println("map" + (isMapVisible ? " opened" : " closed"));
        });
        mapOverviewController.closeButton.setOnAction(click -> {
            mapToggle.reset();
            mapOverview.setVisible(false);
        });

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
