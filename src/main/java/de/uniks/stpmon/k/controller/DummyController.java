package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;


// use this controller for testing view change functionality
public class DummyController extends Controller {

    @FXML
    BorderPane dummyPane;

    @Inject
    public DummyController() {
    }

    @Override
    public Parent render() {
        // do dummy stuff here
        return super.render();
    }
}
