package de.uniks.stpmon.k.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class BackpackMenuEntryController extends Controller {
    @FXML
    public Label inSettIsSelectedLabel;
    @FXML
    public Text ingameSettingText;
    @FXML
    public HBox ingameSettingsEntryHBox;
    final BackpackMenuOption entry;
    final BackpackMenuController backpackMenuController;


    @Inject
    public BackpackMenuEntryController(BackpackMenuController isc, BackpackMenuOption entry) {
        this.entry = entry;
        this.backpackMenuController = isc;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        ingameSettingText.setText(translateString(entry.toString()));
        Platform.runLater(() -> {
            backpackMenuController.setHeight(ingameSettingsEntryHBox.heightProperty().get() + 10);
        });


        parent.setOnMouseClicked(e -> openOption());

        return parent;
    }

    private void openOption() {
        backpackMenuController.openOption(entry);
    }

    public void setArrow() {
        inSettIsSelectedLabel.setText(">");
    }

    public void removeArrow() {
        inSettIsSelectedLabel.setText("");
    }

}
