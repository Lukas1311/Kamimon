package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;


@Singleton
public class RegionListController extends PortalController {

    private final ObservableList<Region> regions = FXCollections.observableArrayList();
    @FXML
    public VBox regionListWrapping;
    @Inject
    RegionService regionService;
    @Inject
    CreateTrainerController createTrainerController;
    @FXML
    public VBox regionListWrappingVBox;
    @FXML
    public GridPane regionListGridPane;
    @FXML
    private ImageView imageViewKamimonLetteringRegion;
    @SuppressWarnings("unused")
    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public RegionListController() {

    }

    private void addRegionToGridPane() {
        regionListGridPane.getChildren().clear();

        for (int i = 0; i < regions.size(); i++) {

            RegionListController listController = this;
            RegionController regionController = new RegionController(regions.get(i), listController);
            Parent parent = regionController.render();
            regionListGridPane.getColumnConstraints().clear();

            int row = i / 3;
            int col = i % 3;

            regionListGridPane.add(parent, col, row);
            System.out.println("Added: " + regions.get(i).name());

            GridPane.setMargin(parent, new Insets(0, 25, 0, 25));
        }
        regionListGridPane.autosize();
    }


    @Override
    public Parent render() {
        final Parent parent = super.render();

        ListChangeListener<Region> listener = c -> addRegionToGridPane();
        regions.addListener(listener);

        disposables.add(regionService.getRegions()
                .observeOn(FX_SCHEDULER)
                .subscribe(regions::setAll, this::handleError));


        loadImage(imageViewKamimonLetteringRegion, "kamimonLettering_new.png");

        regionListWrappingVBox.prefWidthProperty().bind(app.getStage().getScene().widthProperty());


        return parent;
    }

    public void removeLettering() {
        regionListWrapping.getChildren().remove(imageViewKamimonLetteringRegion);
        regionListWrapping.setAlignment(Pos.CENTER);
    }

    @SuppressWarnings("unused")
    public void addLettering() {
        regionListWrapping.getChildren().add(0, imageViewKamimonLetteringRegion);
        regionListWrapping.setAlignment(Pos.CENTER);
    }

    public void createNewTrainer(Region region) {
        subscribe(regionService.getMainTrainer(region._id()), (trainer) -> {
            if (trainer == NoneConstants.NONE_TRAINER) {
                createTrainerController.setChosenRegion(region);
                Parent createTrainer = createTrainerController.render();
                if (regionListWrappingVBox != null && !regionListWrappingVBox.getChildren().contains(createTrainer)) {
                    regionListWrappingVBox.getChildren().clear();
                    regionListWrappingVBox.getChildren().add(createTrainer);
                }
            } else {
                enterRegion(region);
            }
        });
    }
}