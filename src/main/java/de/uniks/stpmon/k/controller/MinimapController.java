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
        return super.render();
    }

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
