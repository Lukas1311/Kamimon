package de.uniks.stpmon.k.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.Objects;

public class IntroductionController extends Controller{


    @FXML
    public ImageView imageIntroduction;
    @FXML
    public Button further;

    @Inject
    public IntroductionController(){

    }

    @Override
    public Parent render() {
        return null;
    }

    public void nextSheet(ActionEvent event) {

    }
}
