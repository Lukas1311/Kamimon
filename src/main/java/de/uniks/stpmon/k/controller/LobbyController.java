package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import javax.inject.Provider;

public class LobbyController extends Controller {
    @FXML
    public Button ingame;
    @FXML
    public Pane pane;
    @Inject
    Provider<HybridController> hybridController;

    @Inject
    LobbyController() {
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        return parent;
    }
}
