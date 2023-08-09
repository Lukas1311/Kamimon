package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.SettingsService;
import de.uniks.stpmon.k.service.SoundService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;

public class SoundController extends Controller{

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

        muteSoundLabel.setText(translateString("muteSound"));

        //back to Settings
        backToSettingButton.setOnAction(click -> backToSettings());

        musicSlider.setValue(settingsService.getSoundValue());
        //save the value with preferences
        listen(musicSlider.valueProperty(),
                (observable, oldValue, newValue) -> {
                    System.out.println("SoundController vol: " + newValue);
                    settingsService.setSoundValue(newValue.floatValue());
                });

        //night mode
        nightMode.setSelected(settingsService.getNightEnabled());
        listen(nightMode.selectedProperty(),
                (observable, oldValue, newValue) -> settingsService.setNightEnabled(newValue));

        return parent;
    }

    public void backToSettings() {
        soundService.init();
        soundService.play();
        //hybridControllerProvider.get().pushTab(SidebarTab.SETTINGS);
    }
}
