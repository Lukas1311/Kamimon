package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import javax.inject.Provider;

public class LobbyController extends Controller {
    @FXML
    public Pane pane;
    @Inject
    Provider<HybridController> hybridController;
    @Inject
    RegionListController regionListController;

    @Inject
    LobbyController() {
    }

    @Override
    public void init() {
        regionListController.init();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        pane.getChildren().add(regionListController.render());

        return parent;
    }
}
