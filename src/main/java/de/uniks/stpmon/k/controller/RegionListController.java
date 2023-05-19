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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RegionListController extends Controller{
    private final ObservableList<Region> regions = FXCollections.observableArrayList();
    private final List<RegionController> controllers = new ArrayList<>();
    @Inject
    RegionApiService regionApiService;
    @FXML
    private BorderPane regionsBorderPane;
    @FXML
    private ImageView imageViewKamimonLetteringRegion;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public RegionListController(){

    }

    @Override
    public void init() {
        disposables.add(regionApiService.getRegions().observeOn(FX_SCHEDULER).subscribe(regions::setAll));
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        final Image imageKamimonLettering = loadImage("kamimonLettering.png");
        imageViewKamimonLetteringRegion.setImage(imageKamimonLettering);
        final ListView<Region> regionListView = new ListView<>(this.regions);
        regionListView.setMaxWidth(200);
        regionsBorderPane.setCenter(regionListView);
        //regionList.getChildren().add(regionListView);
        VBox.setVgrow(regionListView, Priority.ALWAYS);
        regionListView.setCellFactory(e -> new RegionCell(hybridControllerProvider));
        return parent;
    }

    private Image loadImage(String image){
        return new Image(Objects.requireNonNull(LoadingScreenController.class.getResource(image)).toString());
    }
}
