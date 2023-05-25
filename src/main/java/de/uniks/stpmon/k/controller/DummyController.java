package de.uniks.stpmon.k.controller;

import javafx.scene.Parent;

import javax.inject.Inject;


// use this controller for testing view change functionality
public class DummyController extends Controller {

    @Inject
    public DummyController() {
    }

    @Override
    public Parent render() {
        return super.render();
    }
}
