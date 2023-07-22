package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MonDexController extends Controller {

    @FXML
    public AnchorPane monDexPane;

    @Inject
    public MonDexController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        loadBgImage(monDexPane, "inventoryBox.png");

        return parent;
    }
}
