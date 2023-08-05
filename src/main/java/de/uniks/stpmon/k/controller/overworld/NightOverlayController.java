package de.uniks.stpmon.k.controller.overworld;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.SettingsService;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.ClockService;
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
    public ClockService clockService;
    @Inject
    public SettingsService settingsService;
    @Inject
    public WorldRepository world;

    @Inject
    public NightOverlayController() {
    }

    private void applyTime(LocalTime now) {
        if (!settingsService.getNightEnabled()) {
            return;
        }
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
        if (!world.isIndoor()) {
            subscribe(clockService.onTime(), this::applyTime);
            subscribe(settingsService.onNightModusEnabled(), (enabled) -> {
                if (!enabled) {
                    nightOverlay.setVisible(false);
                    return;
                }
                applyTime(clockService.onTime().blockingFirst());
            });
        } else {
            nightOverlay.setVisible(false);
        }
        return render;
    }

    @Override
    public String getResourcePath() {
        return "overworld/";
    }
}
