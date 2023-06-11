package de.uniks.stpmon.k.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;

import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.TextureSetService;
import java.awt.image.BufferedImage;


public class MapOverviewController extends ToastedController {

    @FXML
    BorderPane mapOverviewContent;
    @FXML
    Label regionNameLabel;
    @FXML
    Button closeButton;
    @FXML
    ImageView mapOverviewImage;
    @FXML
    VBox mapContainer;
    @FXML
    Text buildingDescription;

    @Inject
    TextureSetService textureSetService;
    @Inject
    RegionStorage regionStorage;

    // private BufferedImage renderedMap;
    private Image map;

    
    @Inject
    public MapOverviewController() {
    }

    @Override
    public void init() {
        super.init();

        System.out.println("init");
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        mapOverviewContent.setStyle("-fx-background-color: black");

        subscribe(
            textureSetService.createMap(regionStorage.getRegion()),
            tileMap -> {
                System.out.println(tileMap);
                BufferedImage renderedMap = tileMap.renderMap();
                System.out.println("map is rendered");
                map = SwingFXUtils.toFXImage(renderedMap, null);
                mapOverviewImage.setImage(map);
                mapOverviewImage.setFitHeight(300);
                mapOverviewImage.setFitWidth(500);
                mapContainer.setPrefSize(mapOverviewImage.getFitWidth(), mapOverviewImage.getFitHeight());        

            }, err -> {
                handleError(err);
                System.out.println(err);
            }
        );

        // mapOverviewImage.setImage(map);

        // System.out.println("test");

        // mapContainer.getChildren().add(mapOverviewImage);

        closeButton.setOnAction(click -> closeMap());
        return parent;
    }

    public void showInfo() {
    }

    public void closeMap() {
        mapOverviewContent.setVisible(false);
    }
}
