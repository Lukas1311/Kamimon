package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Region;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class RegionController extends Controller {
    private final Region region;
    @FXML
    private Button regionButton;
    @FXML
    private BorderPane regionBorderPane;

    private final RegionListController listController;

    public RegionController(Region region, RegionListController listController) {
        this.region = region;
        this.listController = listController;
    }

    @Override
    public Parent render() {

        final Parent parent = super.render();
        regionButton.setText(region.name());
        regionButton.setOnAction(event -> listController.enterRegion(region));
        return parent;
    }
}
