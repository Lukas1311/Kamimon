package de.uniks.stpmon.k.controller;

import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

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
}
