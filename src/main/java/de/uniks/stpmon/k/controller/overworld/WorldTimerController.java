package de.uniks.stpmon.k.controller.overworld;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldTimerController extends Controller {
    @FXML
    public HBox timer;
    @FXML
    public VBox background;
    @FXML
    public Text label;

    @Inject
    public WorldTimerController() {
    }

    @Override
    public Parent render() {
        Parent render = super.render();
        loadBgImage(background, "overworld/clock_clear.png");
        return render;
    }

    @Override
    public String getResourcePath() {
        return "overworld/";
    }
}
