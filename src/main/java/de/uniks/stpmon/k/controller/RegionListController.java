package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegionListController extends Controller{
    private final App app;
    private final List<Region> regionsList;
    private final List<RegionController> controllers = new ArrayList<>();
    @FXML
    private AnchorPane regions;

    //is needed for dagger
    @Inject
    public RegionListController(App app, List<Region> regionsList){
        this.app = app;
        this.regionsList = regionsList;

    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        generateRegions();
        return parent;
    }

    private void generateRegions() {
        regions.getChildren().clear();
        for(final Region region: regionsList) {
            final RegionController regionController = new RegionController(app, region);
            controllers.add(regionController);
            regionController.init();
            try {
                regions.getChildren().add(regionController.render());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
