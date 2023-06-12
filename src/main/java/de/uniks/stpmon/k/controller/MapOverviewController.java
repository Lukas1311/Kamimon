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

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
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
    Text regionDescription;

    @Inject
    TextureSetService textureSetService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TextDeliveryService textDeliveryService;


    private BufferedImage renderedMap;
    private Image map;
    private Region currentRegion = regionStorage.getRegion();
    
    @Inject
    public MapOverviewController() {
    }

    @Override
    public void init() {
        super.init();

        subscribe(
            textureSetService.createMap(currentRegion),
            tileMap -> {
                System.out.println(tileMap);
                renderedMap = tileMap.renderMap();
                System.out.println("map is rendered");

            }, err -> {
                handleError(err);
                System.out.println(err);
            }
        );

        subscribe(
            textDeliveryService.getTileMapData(currentRegion),
            data -> {
                System.out.println(data);
            }, err -> {
                System.out.println(err);
            }
        );
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        
        regionNameLabel.setText(currentRegion.name());

        map = SwingFXUtils.toFXImage(renderedMap, null);
        mapOverviewImage.setImage(map);
        mapOverviewImage.setFitHeight(300);
        mapOverviewImage.setFitWidth(500);
        mapContainer.setPrefSize(mapOverviewImage.getFitWidth(), mapOverviewImage.getFitHeight());
        mapOverviewContent.setStyle("-fx-background-color: black");

        closeButton.setOnAction(click -> closeMap());
        return parent;
    }

    public void showInfo() {
    }

    public void closeMap() {
        mapOverviewContent.setVisible(false);
    }
}
