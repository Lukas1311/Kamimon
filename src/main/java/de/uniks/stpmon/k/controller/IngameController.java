package de.uniks.stpmon.k.controller;

import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IngameController extends Controller {


    @Inject
    public IngameController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }
}
