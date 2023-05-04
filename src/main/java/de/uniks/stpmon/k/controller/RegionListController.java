package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.views.RegionCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class RegionListController extends Controller{
    private final ObservableList<Region> regionsList = FXCollections.observableArrayList();
    private final List<RegionController> controllers = new ArrayList<>();
    @Inject
    RegionApiService regionApiService;
    @FXML
    private AnchorPane regionsBar;

    @Inject
    public RegionListController(){

    }

    @Override
    public void init() {
        disposables.add(regionApiService.getRegions().observeOn(FX_SCHEDULER).subscribe(regionsList::setAll));
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        final ListView<Region> regions = new ListView<>(this.regionsList);
        regionsBar.getChildren().add(regions);
        VBox.setVgrow(regions, Priority.ALWAYS);
        regions.setCellFactory(e -> new RegionCell());
        return parent;
    }
}
