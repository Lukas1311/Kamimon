package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.SettingsService;
import de.uniks.stpmon.k.service.SoundService;
import de.uniks.stpmon.k.service.world.ScalableClockService;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Duration;
import java.util.function.Function;

public class SoundController extends Controller {

    public static final int[] STEPS = ScalableClockService.STEPS;

    @FXML
    public VBox soundScreen;
    @FXML
    public Button backToSettingButton;
    @FXML
    public Slider musicSlider;
    @FXML
    public CheckBox nightMode;
    @FXML
    public CheckBox muteSound;
    @FXML
    public Label muteSoundLabel;
    @FXML
    public Text mediaControlText;
    @FXML
    public Button previousButton;
    @FXML
    public Button nextButton;
    @FXML
    public Button playPauseButton;
    @FXML
    public Button shuffleButton;
    @FXML
    public Slider dayCycle;
    @FXML
    public Text dayCycleLabel;

    
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    SettingsService settingsService;
    @Inject
    SoundService soundService;

    @Inject
    public SoundController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        mediaControlText.setText(translateString("mediaCtrl"));
        muteSoundLabel.setText(translateString("muteSound"));

        //back to Settings
        backToSettingButton.setOnAction(click -> backToSettings());

        musicSlider.setValue(settingsService.getSoundValue());
        //save the value with preferences
        listen(musicSlider.valueProperty(),
                (observable, oldValue, newValue) -> settingsService.setSoundValue(newValue.floatValue())
        );

        muteSound.setSelected(settingsService.getSoundMuted());
        listen(muteSound.selectedProperty(),
                (observable, oldValue, newValue) -> settingsService.setSoundMuted(newValue)
        );

        previousButton.setOnAction(click -> soundService.previous());
        playPauseButton.setOnAction(click -> soundService.playOrPause());
        nextButton.setOnAction(click -> soundService.next());
        shuffleButton.setOnAction(click -> soundService.shuffle());

        //night mode
        nightMode.setSelected(settingsService.getNightEnabled());
        listen(nightMode.selectedProperty(),
                (observable, oldValue, newValue) -> settingsService.setNightEnabled(newValue)
        );

        // Day time cycle
        dayCycle.setValue(settingsService.getDayTimeCycle());

        Function<Double, String> converter =
                (Double object) -> {
                    Duration duration = getPeriodFromUnit(object);
                    if (duration.toDaysPart() > 0) {
                        return "24h";
                    }
                    return String.format("%02dh:%02dm", duration.toHoursPart(), duration.toMinutesPart());
                };
        dayCycleLabel.setText(translateString("day-cycle",
                converter.apply(Double.valueOf(settingsService.getDayTimeCycle()))));
        listen(dayCycle.valueProperty(),
                (observable, oldValue, newValue) -> {
                    float value = newValue.floatValue();
                    if (!settingsService.setDayTimeCycle(value)) {
                        return;
                    }
                    dayCycleLabel.setText(translateString("day-cycle",
                            converter.apply((double) value)));
                });
        dayCycle.setMin(0);
        dayCycle.setMax(STEPS.length - 1);
        dayCycle.setMajorTickUnit(1);
        dayCycle.setMinorTickCount(0);
        dayCycle.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double object) {
                Duration time = getPeriodFromUnit(object);
                if (time.toDaysPart() > 0) {
                    return "24h";
                }
                return time.toHoursPart() < 1 ? time.toMinutesPart() + "m" : time.toHoursPart() + "h";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        return parent;
    }

    private static Duration getPeriodFromUnit(Double object) {
        return Duration.ofSeconds(ScalableClockService.minutesFromUnit(object.intValue()) * 60L);
    }

    public void backToSettings() {
        hybridControllerProvider.get().pushTab(SidebarTab.SETTINGS);
    }
}
