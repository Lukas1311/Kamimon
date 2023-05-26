package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.NONE;

@Singleton
public class IngameController extends Controller{

    @FXML
    public BorderPane ingame;
    @FXML
    public Pane pane;

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<MonsterBarController> monsterBarControllerProvider;

    @Inject
    public IngameController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        pane.getChildren().add(monsterBarControllerProvider.get().render());
        return parent;
    }

    public void closeSidebar() {
        hybridControllerProvider.get().forceTab(NONE);
    }
}
