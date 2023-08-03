package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.service.SettingsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Locale;
import java.util.Objects;
import java.util.prefs.Preferences;

public class SoundController extends Controller{

    @FXML
    public VBox soundScreen;
    @FXML
    public Button backToSettingButton;
    @FXML
    public Slider music;
    @FXML
    public CheckBox nightMode;
    @FXML
    public Label value;
    @FXML
    public RadioButton germanButton;
    @FXML
    public RadioButton englishButton;
    @FXML
    public ToggleGroup lang;

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    SettingsService settingsService;
    @Inject
    Preferences preferences;
    @Inject
    LoginController loginControllerProvider;


    @Inject
    public SoundController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        soundScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        //back to Settings
        backToSettingButton.setOnAction(click -> backToSettings());

        music.setValue(settingsService.getSoundValue());
        value.setText(String.valueOf((int) music.getValue()));
        //save the value with preferences
        listen(music.valueProperty(),
                (observable, oldValue, newValue) -> {
                    settingsService.setSoundValue(newValue.floatValue());

                    value.setText(String.valueOf((int) music.getValue()));
                });

        //night mode
        nightMode.setSelected(settingsService.getNightEnabled());
        listen(nightMode.selectedProperty(),
                (observable, oldValue, newValue) -> settingsService.setNightEnabled(newValue));

        //GE & EN
        boolean germanSelected = Objects.equals(preferences.get("locale", ""), Locale.GERMAN.toLanguageTag());
        germanButton.setSelected(germanSelected);
        englishButton.setSelected(!germanSelected);
        germanButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                setDe();
            }
        });
        englishButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                setEn();
            }
        });

        return parent;
    }

    public void backToSettings() {
        hybridControllerProvider.get().pushTab(SidebarTab.SETTINGS);
    }

    @FXML
    public void setDe() {
        setLanguage(Locale.GERMAN);
    }

    @FXML
    public void setEn() {
        setLanguage(Locale.ENGLISH);
    }

    private void setLanguage(Locale locale) {
        preferences.put("locale", locale.toLanguageTag());
        hybridControllerProvider.get().pushTab(SidebarTab.SOUND); //reloaded
    }

}
