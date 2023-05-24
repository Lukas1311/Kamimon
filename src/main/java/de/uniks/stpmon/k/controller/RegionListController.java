package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.views.RegionCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Objects;


public class RegionListController extends ToastedController {
    private final ObservableList<Region> regions = FXCollections.observableArrayList();
    @Inject
    RegionApiService regionApiService;
    @FXML
    private BorderPane regionsBorderPane;
    @FXML
    private ImageView imageViewKamimonLetteringRegion;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public RegionListController() {

    }

    @Override
    public void init() {
        disposables.add(regionApiService.getRegions()
                .observeOn(FX_SCHEDULER)
                .subscribe(regions::setAll, this::handleError));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        final Image imageKamimonLettering = loadImage("kamimonLettering.png");
        //imageViewKamimonLetteringRegion.setImage(imageKamimonLettering);
        final ListView<Region> regionListView = new ListView<>(this.regions);
        regionListView.setStyle("-fx-background-color: transparent;");
        //.list-cell { -fx-background-color: transparent;} .list-view {    -fx-background-color: transparent;}
        regionListView.setMaxWidth(200);
        regionsBorderPane.setCenter(regionListView);
        //regionList.getChildren().add(regionListView);
        VBox.setVgrow(regionListView, Priority.ALWAYS);
        regionListView.setCellFactory(e -> new RegionCell(hybridControllerProvider));
        return parent;
    }

    private Image loadImage(String image) {
        return new Image(Objects.requireNonNull(LoadingScreenController.class.getResource(image)).toString());
    }
}
