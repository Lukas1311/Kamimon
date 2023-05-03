package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Region;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javax.inject.Inject;

public class RegionController extends Controller{
    private final Region region;
    private final App app;
    @FXML
    private Button regionButton;
    @Inject
    IngameController ingameController;

    public RegionController(App app, Region region){
        this.app = app;
        this.region = region;
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        //TODO: I need the Model to get the name of the region. I set the Label of the Button with the Id just to look if it works.
        regionButton.setText(region.name());
        regionButton.setOnAction(event -> {
            app.show(ingameController);
        });
        return parent;
    }
}
