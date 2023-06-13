package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.UiToggle;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    MonsterBarController monsterBar;
    @Inject
    MinimapController miniMap;
    @Inject
    MapOverviewController mapOverviewController;
    @Inject
    BackpackController backPack;
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
        monsterBar.init();
        miniMap.init();
        mapOverviewController.init();
        backPack.init();
    }

    @Override
    public void destroy() {
        super.destroy();

        worldController.destroy();
        monsterBar.destroy();
        miniMap.destroy();
        mapOverviewController.destroy();
        backPack.destroy();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        Parent world = this.worldController.render();
        // Null if unit testing world view
        if (world != null) {
            ingameStack.getChildren().add(0, world);
        }
        Parent monsterBar = this.monsterBar.render();
        // Null if unit testing world view
        if (monsterBar != null) {
            pane.getChildren().add(monsterBar);
        }


        Parent miniMap = this.miniMap.render();
        // Null if unit testing world view
        if (miniMap != null) {
            rightVbox.getChildren().add(miniMap);
        }

        Parent mapOverview = this.mapOverviewController.render();


        Parent backPack = this.backPack.render();
        // Null if unit testing world view
        if (backPack != null) {
            rightVbox.getChildren().add(backPack);
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
}
