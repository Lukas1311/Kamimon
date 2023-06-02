package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.views.world.WorldView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.SubScene;
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

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    MonsterBarController monsterBar;

    @Inject
    MinimapController miniMap;

    @Inject
    BackpackController backPack;

    @Inject
    protected WorldView worldView;

    @Inject
    public IngameController() {
    }

    @Override
    public void init() {
        super.init();

        worldView.init();
        monsterBar.init();
        miniMap.init();
        backPack.init();
    }

    @Override
    public void destroy() {
        super.destroy();

        worldView.destroy();
        monsterBar.destroy();
        miniMap.destroy();
        backPack.destroy();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        SubScene scene = worldView.renderScene();
        if (scene != null) {
            ingameStack.getChildren().add(0, scene);

            // Scale the scene to the parent
            scene.widthProperty()
                    .bind(((Region) parent).widthProperty());
            scene.heightProperty()
                    .bind(((Region) parent).heightProperty());
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

        Parent backPack = this.backPack.render();
        // Null if unit testing world view
        if (backPack != null) {
            rightVbox.getChildren().add(backPack);
        }

        return parent;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }
}
