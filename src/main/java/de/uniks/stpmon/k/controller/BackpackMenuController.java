package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.BackpackMenuCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.stpmon.k.controller.BackpackMenuOption.*;

@Singleton
public class BackpackMenuController extends Controller {

    private Controller monBox;
    @FXML
    public ListView<BackpackMenuOption> backpackMenuListView;

    final List<BackpackMenuOption> backpackMenuOptions = new ArrayList<>();
    @FXML
    public Polygon backpackMenuArrow;
    @FXML
    public HBox backpackMenuHBox;

    @Inject
    BackpackController backpackController;
    @Inject
    Provider<MonsterBarController> monsterBarControllerProvider;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    Provider<MonBoxController> monBoxController;

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


    public void openOption(BackpackMenuOption option) {
        switch (option) {
            // delete dummy method after functionality is implemented
            case MONSTER_LIST -> triggerMonBox();
            case MONSTERS -> monsterBarControllerProvider.get().showMonsters();
            case MAP -> dummyMethod();
        }

    }

    public int getId(BackpackMenuOption option) {
        return backpackMenuOptions.indexOf(option);
    }

    private void dummyMethod() {
    }

    public void openMonBox() {
        monBox = monBoxController.get();
        ingameControllerProvider.get().pushController(monBox);
    }

    public void closeMonBox() {
        ingameControllerProvider.get().removeChildren(1);
        monBox = null;
    }

    public void triggerMonBox() {
        if (monBox == null) {
            openMonBox();
        } else {
            closeMonBox();
        }
    }
}
