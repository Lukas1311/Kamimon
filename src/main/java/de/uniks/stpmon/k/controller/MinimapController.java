package de.uniks.stpmon.k.controller;

import javafx.scene.Parent;

import javax.inject.Inject;

public class MinimapController extends Controller{


    @Inject
    public MinimapController(){

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void onDestroy(Runnable action) {
        super.onDestroy(action);
    }
}
