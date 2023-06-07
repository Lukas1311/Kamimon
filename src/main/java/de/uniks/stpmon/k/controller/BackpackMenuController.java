package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.BackpackMenuCellFactory;
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

    int cellId = 0;

    @Inject
    public BackpackMenuController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        backpackMenuOptions.add(MONSTER_LIST);
        backpackMenuOptions.add(MONSTERS);
        backpackMenuOptions.add(MAP);
        backpackMenuListView.setItems(FXCollections.observableArrayList(backpackMenuOptions));

        BackpackMenuCellFactory cellFactory = new BackpackMenuCellFactory(this);
        backpackMenuListView.setCellFactory(cellFactory);

        backpackMenuListView.minHeightProperty().bind(backpackMenuListView.prefHeightProperty());
        backpackMenuListView.maxHeightProperty().bind(backpackMenuListView.prefHeightProperty());

        return parent;
    }

    public void setHeight(double height) {
        double totalHeight = (backpackMenuOptions.size()) * height;
        backpackMenuListView.setPrefHeight(totalHeight);
    }

    protected void openOption(BackpackMenuOption option) {
        //TODO: Open the sub menus
        backpackMenuOptions.stream().filter(e -> e.equals(option)).findFirst();
    }

    public boolean isVisible() {
        return backpackMenuHbox.isVisible();
    }

    public void setVisability(boolean isVisible) {
        backpackMenuHbox.setVisible(isVisible);
    }

    public int getCellId() {
        return this.cellId;
    }

    public void incrementCellId() {
        this.cellId++;
    }
}
