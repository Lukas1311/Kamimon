package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Region;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RegionController extends Controller {
    private final Region region;
    @FXML
    public Text regionNameText;
    @FXML
    public ImageView regionImage;
    @FXML
    public BorderPane imageWrappingPane;
    @FXML
    public VBox regionVBox;

    private final RegionListController listController;


    public RegionController(Region region, RegionListController listController) {
        this.region = region;
        this.listController = listController;

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        int vboxWidth = 400;
        int borderSize = 10;
        regionVBox.setMaxWidth(vboxWidth);
        regionNameText.setText(region.name());
        regionImage.setImage(loadImage("dummyRegionImage.png"));
        regionVBox.setOnMouseClicked(event -> {
            if (!listController.trainerExists()) {
                listController.createNewTrainer(region);
            } else {
                listController.enterRegion(region);
            }
        });
        regionImage.setFitWidth(vboxWidth - borderSize * 2);
        imageWrappingPane.setPrefWidth(400);
        imageWrappingPane.setPrefHeight(regionImage.getFitHeight() + borderSize * 2);

        return parent;
    }

    public Node getNode() {
        return regionVBox;
    }
}
