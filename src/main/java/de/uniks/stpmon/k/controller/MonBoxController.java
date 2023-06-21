package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MonBoxController extends Controller {

    @FXML
    public StackPane monBoxStackPane;
    @FXML
    public GridPane monTeam;
    @FXML
    public GridPane monStorage;
    @FXML
    public ImageView monBoxImage;


    @Inject
    public MonBoxController() {
    }


    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(monBoxImage, "monBox.png");


        return parent;
    }
}
