package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InventoryController extends Controller {
    @FXML
    public AnchorPane fullPane;

    @Inject
     public InventoryController() {
     }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadBgImage(fullPane, "inventory/inv_coins.png");

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }
}
