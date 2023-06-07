package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.IngameSettingsCellFactory;
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

import static de.uniks.stpmon.k.controller.IngameSettingsOption.*;

@Singleton
public class IngameSettingsController extends Controller {
    @FXML
    public ListView<IngameSettingsOption> ingameSettingsListView;

    final List<IngameSettingsOption> ingameSettingOptions = new ArrayList<>();
    @FXML
    public Polygon ingameSettingsArrow;
    @FXML
    public HBox ingameSettingsHBox;

    @Inject
    public IngameSettingsController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        //TODO: Translation
        ingameSettingOptions.add(MONSTER_LIST);
        ingameSettingOptions.add(MONSTERS);
        ingameSettingOptions.add(MAP);
        ingameSettingsListView.setItems(FXCollections.observableArrayList(ingameSettingOptions));

        IngameSettingsCellFactory cellFactory = new IngameSettingsCellFactory(this);
        ingameSettingsListView.setCellFactory(cellFactory);

        ingameSettingsListView.minHeightProperty().bind(ingameSettingsListView.prefHeightProperty());
        ingameSettingsListView.maxHeightProperty().bind(ingameSettingsListView.prefHeightProperty());

        return parent;
    }

    public void setHeight(double height) {
        double totalHeight = (ingameSettingOptions.size()) * height;
        ingameSettingsListView.setPrefHeight(totalHeight);
    }

    protected void openOption(IngameSettingsOption option) {
        //TODO: Open the sub menus
        ingameSettingOptions.stream().filter(e -> e.equals(option)).findFirst();
    }

    public boolean isVisible() {
        return ingameSettingsHBox.isVisible();
    }

    public void setVisability(boolean isVisible) {
        ingameSettingsHBox.setVisible(isVisible);
    }
}
