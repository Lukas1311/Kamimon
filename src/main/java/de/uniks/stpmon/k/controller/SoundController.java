package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.service.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.function.Function;

public class SoundController extends Controller {

    /**
     * Min value is 20 minutes
     */
    public static final int START_OFFSET = 0;
    @FXML
    public VBox soundScreen;
    @FXML
    public Button backToSettingButton;
    @FXML
    public Slider music;
    @FXML
    public CheckBox nightMode;
    @FXML
    public Slider dayCycle;
    @FXML
    public Text dayCycleLabel;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    SettingsService settingsService;

    @Inject
    public SoundController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        //back to Settings
        backToSettingButton.setOnAction(click -> backToSettings());

        music.setValue(settingsService.getSoundValue());
        //save the value with preferences
        listen(music.valueProperty(),
                (observable, oldValue, newValue) -> settingsService.setSoundValue(newValue.floatValue()));

        //night mode
        nightMode.setSelected(settingsService.getNightEnabled());
        listen(nightMode.selectedProperty(),
                (observable, oldValue, newValue) -> settingsService.setNightEnabled(newValue));

        // Day time cycle
        dayCycle.setValue(settingsService.getDayTimeCycle());

        Function<Double, String> converter =
                (Double object) -> {
                    int precision = (object.intValue() + START_OFFSET) % 3;
                    return (object.intValue() + START_OFFSET) / 3 + ":" + (precision == 0 ? "00" : precision * 20) + "h";
                };
        dayCycleLabel.setText(translateString("day-cycle",
                converter.apply(Double.valueOf(settingsService.getDayTimeCycle()))));
        listen(dayCycle.valueProperty(),
                (observable, oldValue, newValue) -> {
                    float value = newValue.floatValue();
                    if (value < 1) {
                        dayCycle.setValue(1);
                        return;
                    }
                    if (settingsService.setDayTimeCycle(value)) {
                        dayCycleLabel.setText(translateString("day-cycle",
                                converter.apply((double) value)));
                    }
                });
        dayCycle.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double object) {
                return (object.intValue() + START_OFFSET) / 3 + "h";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        return parent;
    }

    public void backToSettings() {
        hybridControllerProvider.get().pushTab(SidebarTab.SETTINGS);
    }
}
