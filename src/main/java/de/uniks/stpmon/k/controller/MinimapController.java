package de.uniks.stpmon.k.controller;

import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;

public class MinimapController extends Controller{

    @Inject
    Provider<MapOverviewController> mapOverviewControllerProvider; 


    @Inject
    public MinimapController(){

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }

    @Override // TODO: is this necessary?
    public void init() {
        super.init();
    }

    @Override // TODO: is this necessary?
    public void destroy() {
        super.destroy();
    }

    @Override // TODO: is this necessary?
    public void onDestroy(Runnable action) {
        super.onDestroy(action);
    }

    public void openMapOverview() {
        app.show(mapOverviewControllerProvider.get());
    }
}
