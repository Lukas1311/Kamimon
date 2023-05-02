package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Region;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class RegionController extends Controller{
    private final Region region;
    private final App app;
    @FXML
    private Button regionButton;

    public RegionController(App app, Region region){
        this.app = app;
        this.region = region;
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        regionButton.setText(region.name());
        regionButton.setOnAction(event -> {
            app.show(new IngameController(app));
        });
        return parent;
    }
}
