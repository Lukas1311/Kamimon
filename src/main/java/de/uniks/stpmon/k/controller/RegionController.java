package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

import javax.inject.Inject;

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
        //TODO: I need the Model to get the name of the region. I set the Label of the Button with the Id just to look if it works.
        regionButton.setText(region.getId());
        regionButton.setOnAction(event -> {
            app.show(new IngameController(app));
        });
        return parent;
    }
}
