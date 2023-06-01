package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

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
    Provider<HybridController> hybridControllerProvider;

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
        // This line is for testing purposes when a trainer is available
        hybridControllerProvider.get().openMain(INGAME);
    }
}
