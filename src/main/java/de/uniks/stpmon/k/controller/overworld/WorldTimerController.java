package de.uniks.stpmon.k.controller.overworld;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.world.ClockService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class WorldTimerController extends Controller {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    @FXML
    public HBox timer;
    @FXML
    public VBox background;
    @FXML
    public Text label;
    @Inject
    public ClockService clockService;

    @Inject
    public WorldTimerController() {
    }

    private void applyTime(LocalTime now) {
        label.setText(formatter.format(now));
    }

    @Override
    public Parent render() {
        Parent render = super.render();
        loadBgImage(background, "overworld/clock_clear.png");
        subscribe(clockService.onTime(), this::applyTime);
        return render;
    }

    @Override
    public String getResourcePath() {
        return "overworld/";
    }
}
