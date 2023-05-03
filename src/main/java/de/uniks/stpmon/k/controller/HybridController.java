package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Provider;

public class HybridController extends Controller {
    @FXML
    public HBox hBox;
    @FXML
    public Pane pane;
    @FXML
    public StackPane stackPane;
    @Inject
    Provider<SidebarController> sidebarController;

    @Inject
    public HybridController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        pane.getChildren().add(sidebarController.get().render());
        stackPane.getChildren().add(new LoginController().render());
        return parent;
    }

    public void openChat() {
    }

}