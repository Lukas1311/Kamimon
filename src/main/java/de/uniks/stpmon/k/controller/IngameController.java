package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class IngameController extends Controller {

    @FXML
    public BorderPane ingame;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public IngameController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().closeTab();
    }
}
