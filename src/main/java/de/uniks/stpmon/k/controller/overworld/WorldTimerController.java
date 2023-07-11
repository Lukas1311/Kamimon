package de.uniks.stpmon.k.controller.overworld;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.world.WorldService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

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
    public WorldService worldService;

    @Inject
    public WorldTimerController() {
    }

    @Override
    public Parent render() {
        Parent render = super.render();
        loadBgImage(background, "overworld/clock_clear.png");
        applyTime();
        return render;
    }

    @Override
    public void init() {
        super.init();
        LocalTime currentTime = worldService.getCurrentTime();
        int offsetSecond = 60 - currentTime.getSecond();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                applyTime();
            }
        }, offsetSecond * 1000, 1000);
        onDestroy(timer::cancel);
    }

    private void applyTime() {
        LocalTime currentTime = worldService.getCurrentTime();
        label.setText(formatter.format(currentTime));
    }

    @Override
    public String getResourcePath() {
        return "overworld/";
    }
}
