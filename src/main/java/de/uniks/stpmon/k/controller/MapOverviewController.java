package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import javax.inject.Inject;


public class MapOverviewController extends Controller {

    @FXML
    BorderPane mapOverviewContent;
    @FXML
    Label regionNameLabel;
    @FXML
    Button closeButton;
    @FXML
    ImageView mapOverviewImage;
    @FXML
    Text buildingDescription;

    
    @Inject
    public MapOverviewController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }

    public void showInfo() {

    }
}
