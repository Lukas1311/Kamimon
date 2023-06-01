package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.views.RegionCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;


public class RegionListController extends ToastedController {
    private final ObservableList<Region> regions = FXCollections.observableArrayList();
    @Inject
    RegionService regionService;
    @FXML
    private BorderPane regionsBorderPane;
    @FXML
    private ImageView imageViewKamimonLetteringRegion;

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    CreateTrainerController createTrainerController;

    private boolean isNewTrainer = true;

    @Inject
    public RegionListController() {

    }

    @Override
    public void init() {
        disposables.add(regionService.getRegions()
                .observeOn(FX_SCHEDULER)
                .subscribe(regions::setAll, this::handleError));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        imageViewKamimonLetteringRegion.setImage(loadImage("kamimonLettering.png"));
        final ListView<Region> regionListView = new ListView<>(this.regions);
        regionListView.setStyle("-fx-background-color: transparent;");
        regionListView.setMaxWidth(200);
        regionsBorderPane.setCenter(regionListView);
        VBox.setVgrow(regionListView, Priority.ALWAYS);
        regionListView.setCellFactory(e -> new RegionCell(this));
        return parent;
    }

    /**
     * Check if the user has already created a trainer
     * If not created, will show createTrainer screen
     * If created, will go straight to the game
     */
    public void openRegion(Region region) {
        if (isNewTrainer) {
            Parent createTrainer = createTrainerController.render();
            if (regionsBorderPane != null && !regionsBorderPane.getChildren().contains(createTrainer)) {
                regionsBorderPane.setCenter(createTrainer);
            }
        } else {
            subscribe(regionService.enterRegion(region),
                    (area) -> hybridControllerProvider.get().openMain(INGAME));
        }
    }

    public void setNewTrainer (boolean newTrainer) {
        this.isNewTrainer = newTrainer;
    }
}
