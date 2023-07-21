package de.uniks.stpmon.k.controller.lobby;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.controller.CreateTrainerController;
import de.uniks.stpmon.k.controller.PortalController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;


@Singleton
public class RegionListController extends PortalController {
    @FXML
    public VBox regionListWrapping;
    @FXML
    public VBox regionListWrappingVBox;
    @FXML
    public FlowPane regionsFlowPane;
    @FXML
    private ImageView imageViewKamimonLetteringRegion;
    @Inject
    RegionService regionService;
    @Inject
    CreateTrainerController createTrainerController;

    @Inject
    public RegionListController() {
    }

    private void addRegionsToFlowPane(List<Region> regions) {
        regionsFlowPane.getChildren().clear();

        for (int i = 0; i < regions.size(); i++) {
            if (i > 2) {
                continue;
            }
            int finalI = i;
            subscribe(regionService.getRegionImage(regions.get(i)._id()), (regionImage) -> {
                Image image = ImageUtils.scaledImageFX(regionImage.grayscaleImage(), 1);
                RegionController regionController = new RegionController(regions.get(finalI),
                        this, image);
                Parent parent = regionController.render();

                regionsFlowPane.getChildren().add(parent);

                FlowPane.setMargin(parent, new Insets(25, 25, 0, 25));
            });
        }
        regionsFlowPane.autosize();
    }


    @Override
    public Parent render() {
        final Parent parent = super.render();

        subscribe(regionService.getRegions(), this::addRegionsToFlowPane);

        loadImage(imageViewKamimonLetteringRegion, "kamimonLettering_new.png");

        regionListWrappingVBox.prefWidthProperty().bind(app.getStage().getScene().widthProperty());

        return parent;
    }

    public void removeLettering() {
        regionListWrapping.getChildren().remove(imageViewKamimonLetteringRegion);
        regionListWrapping.setAlignment(Pos.CENTER);
    }

    public void createNewTrainer(Region region) {
        subscribe(regionService.getMainTrainer(region._id()), (trainer) -> {
            if (trainer == NoneConstants.NONE_TRAINER) {
                removeLettering();
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