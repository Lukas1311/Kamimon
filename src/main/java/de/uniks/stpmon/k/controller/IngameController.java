package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.map.WorldController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IngameController extends Controller {

    @FXML
    public StackPane ingameStack;
    @FXML
    public VBox ingame;
    @FXML
    public Text inGameText;

    @Inject
    WorldController worldController;

    @Inject
    public IngameController() {
    }

    @Override
    public void init() {
        super.init();
        worldController.init();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        ingameStack.getChildren().add(0, worldController.render());
        return parent;
    }
}
