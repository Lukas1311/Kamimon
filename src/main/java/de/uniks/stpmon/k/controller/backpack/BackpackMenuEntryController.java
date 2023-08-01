package de.uniks.stpmon.k.controller.backpack;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BackpackMenuEntryController extends Controller {

    @FXML
    public Label backpackMenuSelectedLabel;
    @FXML
    public HBox backpackMenuEntryHBox;
    @FXML
    public Label backpackMenuLabel;

    private final BackpackMenuOption entry;
    private final BackpackMenuController backpackMenuController;

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

    @Override
    public String getResourcePath() {
        return "backpack/";
    }
}
