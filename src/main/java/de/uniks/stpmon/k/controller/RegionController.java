package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class RegionController extends Controller {
    private final Region region;
    @FXML
    private Button regionButton;
    @FXML
    private BorderPane regionBorderPane;

    private final Provider<HybridController> hybridControllerProvider;

    public RegionController(Region region, Provider<HybridController> hybridControllerProvider) {
        this.region = region;
        this.hybridControllerProvider = hybridControllerProvider;
    }

    @Override
    public Parent render() {

        final Parent parent = super.render();
        regionButton.setText(region.name());
        regionButton.setOnAction(event -> {
            hybridControllerProvider.get().openMain(INGAME);
        });
        return parent;
    }
}
