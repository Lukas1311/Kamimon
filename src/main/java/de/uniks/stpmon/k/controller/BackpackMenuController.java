package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.BackpackMenuCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.stpmon.k.controller.BackpackMenuOption.*;

@Singleton
public class BackpackMenuController extends Controller {
    @FXML
    public ListView<BackpackMenuOption> backpackMenuListView;

    final List<BackpackMenuOption> backpackMenuOptions = new ArrayList<>();
    @FXML
    public Polygon backpackMenuArrow;
    @FXML
    public HBox backpackMenuHbox;

    @Inject
    public BackpackMenuController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        if (backpackMenuOptions.isEmpty()) {
            backpackMenuOptions.add(MONSTER_LIST);
            backpackMenuOptions.add(MONSTERS);
            backpackMenuOptions.add(MAP);
        }
        backpackMenuListView.setCellFactory(param -> new BackpackMenuCell(this));
        double totalHeight = (backpackMenuOptions.size()) * 33;
        backpackMenuListView.setPrefHeight(totalHeight);

        backpackMenuListView.minHeightProperty().bind(backpackMenuListView.prefHeightProperty());
        backpackMenuListView.maxHeightProperty().bind(backpackMenuListView.prefHeightProperty());
        backpackMenuListView.setItems(FXCollections.observableArrayList(backpackMenuOptions));


        return parent;
    }


    protected void openOption(BackpackMenuOption option) {
        switch (option) {
            // delete dummy method after functionality is implemented
            case MONSTER_LIST -> dummyMethod();
            case MONSTERS -> dummyMethod();
            case MAP -> dummyMethod();
        }
    }

    public int getId(BackpackMenuOption option) {
        return backpackMenuOptions.indexOf(option);
    }

    private void dummyMethod() {
    }
}