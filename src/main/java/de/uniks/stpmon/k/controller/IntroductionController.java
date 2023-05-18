package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Objects;



public class IntroductionController extends Controller{


    @FXML
    public ImageView imageIntroduction;
    @FXML
    public Button further;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public IntroductionController(){

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        return parent;
    }

    public void nextSheet(ActionEvent event) {
        app.show(hybridControllerProvider.get());
    }
}
