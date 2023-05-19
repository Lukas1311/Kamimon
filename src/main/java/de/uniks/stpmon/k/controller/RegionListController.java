package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
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
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;


public class RegionListController extends ToastedController{
    private final ObservableList<Region> regions = FXCollections.observableArrayList();
    @Inject
    RegionApiService regionApiService;
    @FXML
    private AnchorPane regionList;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public RegionListController(){

    }

    @Override
    public void init() {
        disposables.add(regionApiService.getRegions()
                .observeOn(FX_SCHEDULER)
                .subscribe(regions::setAll, this::handleError));
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        final ListView<Region> regions = new ListView<>(this.regions);
        regionList.getChildren().add(regions);
        VBox.setVgrow(regions, Priority.ALWAYS);
        regions.setCellFactory(e -> new RegionCell(hybridControllerProvider));
        return parent;
    }
}
