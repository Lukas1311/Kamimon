package de.uniks.stpmon.k.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ResourceBundle;

public class BackpackMenuEntryController extends Controller {
    @FXML
    public Label backpackMenuSelectedLabel;
    @FXML
    public Text backpackMenuText;
    @FXML
    public HBox backpackMenuHbox;
    final BackpackMenuOption entry;
    final BackpackMenuController backpackMenuController;

    @Inject
    protected Provider<ResourceBundle> resources;


    @Inject
    public BackpackMenuEntryController(BackpackMenuController isc, BackpackMenuOption entry) {
        this.entry = entry;
        this.backpackMenuController = isc;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        setIds();
        backpackMenuText.setText(backpackMenuController.translateString(entry.toString()));

        Platform.runLater(() -> backpackMenuController.setHeight(backpackMenuHbox.heightProperty().get() + 10));


        parent.setOnMouseClicked(e -> openOption());

        return parent;
    }

    private void setIds() {
        //only set id's once
        backpackMenuText.setId("backpackMenuText" + backpackMenuController.getCellId());
        backpackMenuSelectedLabel.setId("backpackMenuSelectedLabel" + backpackMenuController.getCellId());
        backpackMenuController.incrementCellId();
    }

    private void openOption() {
        backpackMenuController.openOption(entry);
    }

    public void setArrow() {
        backpackMenuSelectedLabel.setText(">");
    }

    public void removeArrow() {
        backpackMenuSelectedLabel.setText("");
    }

}
