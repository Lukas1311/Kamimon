package de.uniks.stpmon.k.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class IngameSettingsEntryController extends Controller {
    @FXML
    public Label inSettIsSelectedText;
    @FXML
    public Text ingameSettingText;
    @FXML
    public HBox ingameSettingsEntryHBox;
    final String entryText;
    final IngameSettingsController ingameSettingsController;


    @Inject
    public IngameSettingsEntryController(IngameSettingsController isc, String entryText) {
        this.entryText = entryText;
        this.ingameSettingsController = isc;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        ingameSettingText.setText(entryText);
        Platform.runLater(() -> {
            ingameSettingsController.setHeight(ingameSettingsEntryHBox.heightProperty().get() + 10);
        });


        parent.setOnMouseClicked(e -> openOption());

        return parent;
    }

    private void openOption() {
        ingameSettingsController.openOption(entryText);
    }

}
