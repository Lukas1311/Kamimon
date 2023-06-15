package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

public class LobbyController extends Controller {
    @FXML
    public Pane lobbyPane;
    @Inject
    Provider<HybridController> hybridControllerProvider;
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

        lobbyPane.getChildren().add(regionListController.render());

        return parent;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }
}
