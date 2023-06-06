package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
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
public class IngameController extends Controller {

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
    MonsterBarController monsterBar;

    @Inject
    MinimapController miniMap;

    @Inject
    BackpackController backPack;

    @Inject
    Provider<IngameSettingsController> ingameSettingsControllerProvider;

    @Inject
    protected WorldController worldController;

    Parent ingameSettings;

    @Inject
    public IngameController() {
    }

    @Override
    public void init() {
        super.init();

        worldController.init();
        monsterBar.init();
        miniMap.init();
        backPack.init();
    }

    @Override
    public void destroy() {
        super.destroy();

        worldController.destroy();
        monsterBar.destroy();
        miniMap.destroy();
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
            rightVbox.getChildren().add(0, miniMap);
        }

        Parent backPack = this.backPack.render();
        // Null if unit testing world view
        if (backPack != null) {
            ingameWrappingHBox.getChildren().add(backPack);
            backPack.setOnMouseClicked(click -> {
                if (ingameSettings == null) {
                    ingameSettings = ingameSettingsControllerProvider.get().render();
                    ingameWrappingHBox.getChildren().add(0, ingameSettings);
                    //ingameStack.getChildren().add(ingameSettings);
                    ingameStack.setAlignment(Pos.TOP_RIGHT);

                } else {
                    ingameSettingsControllerProvider.get().setVisability(
                            !ingameSettingsControllerProvider.get().isVisible());
                }


            });
        }

        return parent;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }
}
