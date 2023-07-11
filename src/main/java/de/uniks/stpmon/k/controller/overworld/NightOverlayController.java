package de.uniks.stpmon.k.controller.overworld;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.world.FastClock;
import de.uniks.stpmon.k.service.world.WorldService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalTime;

@Singleton
public class NightOverlayController extends Controller {


    @FXML
    public BorderPane nightOverlay;

    @Inject
    public WorldService worldService;
    @Inject
    public FastClock clockService;


    @Inject
    public NightOverlayController() {
    }

    private void applyTime(LocalTime now) {
        float factor = worldService.getNightFactor(now);
        if (factor > 0) {
            nightOverlay.setOpacity(factor);
            nightOverlay.setVisible(true);
        } else {
            nightOverlay.setVisible(false);
        }
    }

    @Override
    public Parent render() {
        Parent render = super.render();
        subscribe(clockService.onTime(), this::applyTime);
        return render;
    }

    @Override
    public String getResourcePath() {
        return "overworld/";
    }
}
