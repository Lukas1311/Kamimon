package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;

public class PauseController extends Controller {

    @FXML
    public BorderPane pauseScreen;

    @Inject
    public PauseController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }
}
