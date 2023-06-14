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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import java.awt.image.BufferedImage;


@Singleton
public class MapOverviewController extends ToastedController {

    @FXML
    BorderPane mapOverviewContent;
    @FXML
    Label regionNameLabel;
    @FXML
    public Button closeButton;
    @FXML
    ImageView mapImageView;
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

    private final double MAP_OVERVIEW_SCALE = 0.8; // scale the map container to 80% of screen
    private Image map;
    private Region currentRegion;
    
    @Inject
    public MapOverviewController() {
    }

    @Override
    public void init() {
        super.init();

        currentRegion = regionStorage.getRegion();

        if (currentRegion.map() != null) {
            subscribe(
                textDeliveryService.getRouteData(currentRegion),
                routeListData -> {
                        routeListData.stream()
                            .filter(routeData -> routeData.id() == 65) // e.g. id = 65 for Coupe Archipelago
                            .forEach(System.out::println);
                    // TODO: @halber01 maybe you can also calculate the current position of the player and show him on the map
                    // TODO: here you could filter each route for id or whatever
                    // and then calculate the position on the map with the given values inside the data
                    // e.g. the 3 little isles on the left of Albertania are the "Coupe Archipelago"
                    // and they have these values height=0, width=0, x=16, y=112
                }, err -> {
                    err.printStackTrace();
                    handleError(err);
                    // TODO: if you need to set fxelements with text then better put this whole method into render
                }
            );
        }

    }


    @Override
    public Parent render() {
        final Parent parent = super.render();
        
        regionNameLabel.setText(currentRegion.name());
        regionNameLabel.setFont(new Font(20));

        if (currentRegion.map() != null) {
            disposables.add(
                textureSetService.createMap(currentRegion)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(tileMap -> {
                        BufferedImage renderedMap = tileMap.renderMap();
                        map = SwingFXUtils.toFXImage(renderedMap, null);
                        mapImageView.setImage(map);
                        mapImageView.fitWidthProperty().bind(mapOverviewContent.widthProperty().multiply(MAP_OVERVIEW_SCALE));
                        mapImageView.fitHeightProperty().bind(mapOverviewContent.heightProperty().multiply(MAP_OVERVIEW_SCALE));
                        mapContainer.setPrefSize(mapImageView.getFitWidth(), mapImageView.getFitHeight());
                    }, this::handleError)
            );
            // subscribe(
            //     textureSetService.createMap(currentRegion),
            //     tileMap -> {
            //         BufferedImage renderedMap = tileMap.renderMap();
            //         map = SwingFXUtils.toFXImage(renderedMap, null);
            //         mapImageView.setImage(map);
            //         mapImageView.fitWidthProperty().bind(mapOverviewContent.widthProperty().multiply(MAP_OVERVIEW_SCALE));
            //         mapImageView.fitHeightProperty().bind(mapOverviewContent.heightProperty().multiply(MAP_OVERVIEW_SCALE));
            //         mapContainer.setPrefSize(mapImageView.getFitWidth(), mapImageView.getFitHeight());
            //     }, err -> {
            //         handleError(err);
            //     }
            // );
        }
        
        Window parentWindow = app.getStage().getScene().getWindow();
        mapOverviewContent.prefWidthProperty().bind(parentWindow.widthProperty().multiply(MAP_OVERVIEW_SCALE));
        mapOverviewContent.prefHeightProperty().bind(parentWindow.heightProperty().multiply(MAP_OVERVIEW_SCALE));

        mapOverviewContent.setStyle("-fx-background-color: black");
        System.out.println("test");


        closeButton.setOnAction(click -> closeMap());
        return parent;
    }

    public void showInfo() {
    }

    public void closeMap() {
        // mapOverviewContent.setVisible(false); // TODO: unused (used already in ingame)
    }
}
