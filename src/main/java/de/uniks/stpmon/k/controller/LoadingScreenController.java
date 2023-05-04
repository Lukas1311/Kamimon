package de.uniks.stpmon.k.controller;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.Objects;

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
        final Image imageKamimonLettering = loadImage("kamimonLettering.png");
        imageViewKamimonLettering.setImage(imageKamimonLettering);

        final Image imageDeadBirdsSociety = loadImage("deadBirdsSocietyLogo.png");
        imageViewDeadBirdsSociety.setImage(imageDeadBirdsSociety);

        final Image imageKgmLogo = loadImage("kgmLogo.png");
        imageViewKgmLogo.setImage(imageKgmLogo);


        return parent;
    }

    private Image loadImage(String image){
        return new Image(Objects.requireNonNull(LoadingScreenController.class.getResource(image)).toString());
    }
}
