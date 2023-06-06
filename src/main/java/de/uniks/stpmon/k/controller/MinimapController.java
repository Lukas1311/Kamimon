package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;

public class MinimapController extends Controller{

    @FXML
    public ImageView miniMap;

    @Inject
    Provider<MapOverviewController> mapOverviewControllerProvider; 


    @Inject
    public MinimapController(){

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        miniMap.setOnMouseClicked(click -> openMapOverview());

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
        System.out.println("map is opened");
        app.show(mapOverviewControllerProvider.get());
    }
}
