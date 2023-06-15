package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

public class PauseController extends Controller {

    @FXML
    public BorderPane pauseScreen;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public PauseController() {
    }

    @Override
    public Parent render() {
        return super.render();
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }
}
