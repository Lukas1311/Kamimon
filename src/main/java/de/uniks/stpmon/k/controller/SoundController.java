package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
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
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Preferences preferences;


    @Inject
    public SoundController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        //back to Settings
        backToSettingButton.setOnAction(click -> backToSettings());

        music.setValue(preferences.getDouble("music", 0));

        //save the value with preferences
        music.valueProperty().addListener((observable, oldValue, newValue) -> {
            preferences.putDouble("music", (Double) newValue);
        });

        return parent;
    }

    public void backToSettings() {
        hybridControllerProvider.get().pushTab(SidebarTab.SETTINGS);
    }
}
