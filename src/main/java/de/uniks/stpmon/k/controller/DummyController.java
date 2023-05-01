package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;

import javax.inject.Inject;


// use this controller for testing view change functionality
public class DummyController extends Controller {
    
    @Inject
    public DummyController(){}

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }
}
