package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class RegionListController extends Controller{
    private final ObservableList<Region> regionsList = FXCollections.observableArrayList();
    private final List<RegionController> controllers = new ArrayList<>();
    private final RegionApiService regionApiService;
    @FXML
    private AnchorPane regions;

    @Inject
    public RegionListController(RegionApiService regionApiService){
        this.regionApiService = regionApiService;

    }

    @Override
    public void init() {
        disposables.add(regionApiService.getRegions().subscribe(this.regionsList::setAll));
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
            final RegionController regionController = new RegionController(region);
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
