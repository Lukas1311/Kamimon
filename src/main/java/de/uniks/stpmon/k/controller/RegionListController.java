package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
    public VBox regionListWrappingVox;
    private int colIndex;
    @FXML
    public GridPane regionListGridPane;
    @FXML
    private ImageView imageViewKamimonLetteringRegion;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public RegionListController() {

    }

    private void addRegionToGridPane() {
        RegionListController listController = this;
        RegionController regionController = new RegionController(regions.get(colIndex), listController);
        Parent parent = regionController.render();
        ColumnConstraints column = new ColumnConstraints(200, 200, Double.MAX_VALUE);
        column.setHgrow(Priority.ALWAYS);
        column.setHalignment(HPos.CENTER);
        regionListGridPane.getColumnConstraints().clear();
        regionListGridPane.getColumnConstraints().add(column);
        regionListGridPane.add(parent, colIndex, 0);

    }

    @Override
    public void init() {
        super.init();
        colIndex = 0;
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

        regionListWrappingVox.prefWidthProperty().bind(app.getStage().getScene().widthProperty());
        return parent;
    }

    public void removeLettering(){

    }

    public void addLettering(){
        regionListWrapping.getChildren().remove(imageViewKamimonLetteringRegion);
        regionListWrapping.setAlignment(Pos.CENTER);
    }

    public void createNewTrainer(Region region) {
        subscribe(regionService.getMainTrainer(region._id()), (trainer) -> {
            if (trainer == NoneConstants.NONE_TRAINER) {
                createTrainerController.setChosenRegion(region);
                Parent createTrainer = createTrainerController.render();
                if (regionListWrappingVox != null && !regionListWrappingVox.getChildren().contains(createTrainer)) {
                    regionListWrappingVox.getChildren().clear();
                    regionListWrappingVox.getChildren().add(createTrainer);
                }
            } else {
                enterRegion(region);
            }
        });

    }
}