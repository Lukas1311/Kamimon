package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ResourceBundle;

public class BackpackMenuEntryController extends Controller {

    @FXML
    public Label backpackMenuSelectedLabel;
    @FXML
    public HBox backpackMenuEntryHBox;
    final BackpackMenuOption entry;
    final BackpackMenuController backpackMenuController;
    @FXML
    public Label backpackMenuLabel;

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
        backpackMenuLabel.setText(backpackMenuController.translateString(entry.toString()));


        return parent;
    }

    private void setIds() {
        //only set id's once
        int id = backpackMenuController.getId(entry);
        backpackMenuLabel.setId("backpackMenuLabel_" + id);
        backpackMenuSelectedLabel.setId("backpackMenuSelectedLabel_" + id);
    }

    public void setArrow() {
        backpackMenuSelectedLabel.setText(">");
    }

    public void removeArrow() {
        backpackMenuSelectedLabel.setText("");
    }

}
