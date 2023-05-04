package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Region;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javax.inject.Inject;

public class RegionController extends Controller{
    private final Region region;
    @FXML
    private Button regionButton;

    private final App app;

    public RegionController(Region region, App app){
        this.region = region;
        this.app = app;
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        regionButton.setText(region.name());
        regionButton.setOnAction(event -> {
            app.show(new IngameController());
        });
        return parent;
    }
}
