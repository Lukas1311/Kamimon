package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

public class PauseController extends Controller {

    @FXML
    public BorderPane pauseScreen;
    @FXML
    public Text sidebarText;
    @FXML
    public Text ingameText;
    @FXML
    public GridPane shortcutPane;
    @FXML
    public Text pauseText;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public PauseController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(pauseScreen, "background_black.png");
        return parent;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }

}
