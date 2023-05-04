package de.uniks.stpmon.k.controller;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;

import javax.inject.Inject;
import java.net.URL;

public class LoadingScreenController extends Controller{

    @FXML
    public ImageView imageViewKamimonLettering;
    @FXML
    public ImageView imageViewDeadBirdsSociety;
    @FXML
    public ImageView imageViewKgmLogo;

    @Inject
    public LoadingScreenController(){

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        final Image imageKamimonLettering = new Image(LoadingScreenController.class.getResource("kamimonLettering.png").toString());
        imageViewKamimonLettering.setImage(imageKamimonLettering);

        final Image imageDeadBirdsSociety = new Image(LoadingScreenController.class.getResource("deadBirdsSocietyLogo.png").toString());
        imageViewDeadBirdsSociety.setImage(imageDeadBirdsSociety);

        final Image imageKgmLogo = new Image(LoadingScreenController.class.getResource("kgmLogo.png").toString());
        imageViewKgmLogo.setImage(imageKgmLogo);


        return parent;
    }
}
