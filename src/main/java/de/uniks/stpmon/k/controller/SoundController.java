package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.service.SettingsService;
import de.uniks.stpmon.k.service.world.ScalableClockService;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class SoundController extends Controller {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

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
                (Double object) -> formatter.format(getPeriodFromUnit(object));
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
        dayCycle.setMin(0);
        dayCycle.setMax(ScalableClockService.STEPS.length - 1);
        dayCycle.setMajorTickUnit(1);
        dayCycle.setMinorTickCount(0);
        dayCycle.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double object) {
                LocalTime time = getPeriodFromUnit(object);
                return time.getHour() < 1 ? time.getMinute() + "m" : time.getHour() + "h";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        return parent;
    }

    private static LocalTime getPeriodFromUnit(Double object) {
        return LocalTime.ofSecondOfDay(ScalableClockService.STEPS[
                Math.min(Math.max(object.intValue(), 0), ScalableClockService.STEPS.length - 1)]
                * ScalableClockService.STEP_UNIT_IN_MINUTES * 60L);
    }

    public void backToSettings() {
        hybridControllerProvider.get().pushTab(SidebarTab.SETTINGS);
    }
}
