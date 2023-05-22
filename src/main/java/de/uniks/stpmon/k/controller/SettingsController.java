package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import javax.inject.Inject;

public class SettingsController extends Controller {

    @FXML
    public VBox settingsScreen;
    @FXML
    public Button backButton;
    @FXML
    public ImageView userSprite;

    @Inject
    public SettingsController() {
    }

    public Parent render() {
        final Parent parent = super.render();
        settingsScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));
        Rectangle rectangle = new Rectangle(0, 0, 200, 150);
        rectangle.setArcWidth(20);
        rectangle.setArcHeight(20);
        userSprite.setClip(rectangle);
        return parent;
    }
}
