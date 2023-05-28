package de.uniks.stpmon.k.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class ChooseSpriteController extends Controller {
    @FXML
    public Text chooseTrainer;
    @FXML
    public Button SpriteLeft;
    @FXML
    public ImageView SpriteImage;
    @FXML
    public Button SpriteRight;
    @FXML
    public Button saveSprite;

    @Inject
    public ChooseSpriteController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        chooseTrainer.setText(translateString("choose_trainer"));
        saveSprite.setText(translateString("saveChanges"));
        return parent;
    }

    public void saveSprite(ActionEvent actionEvent) {
    }
}
