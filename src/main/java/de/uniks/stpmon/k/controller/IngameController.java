package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.map.WorldController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    public Text inGameText;

    @Inject
    WorldController worldController;

    @Inject
    protected Provider<HybridController> hybridControllerProvider;

    @Inject
    public IngameController() {
    }

    @Override
    public void init() {
        super.init();
        worldController.init();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        ingameStack.getChildren().add(0, worldController.render());
        return parent;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }
}
