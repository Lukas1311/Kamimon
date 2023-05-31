package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javax.inject.Inject;

public class CreateTrainerController extends Controller {
    @FXML
    public TextField createTrainerInput;
    @FXML
    public ImageView trainerSprite;
    @FXML
    public Button createSpriteButton;
    @FXML
    public Button createTrainerButton;

    @Inject
    public CreateTrainerController() {}

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }

    public void trainerSprite() {
    }

    public void createSprite() {
    }

    public void createTrainer() {
    }
}
