package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.IngameSettingsCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class IngameSettingsController extends Controller {
    @FXML
    public ListView<String> ingameSettingsListView;

    final List<String> ingameSettingOptions = new ArrayList<>();
    @FXML
    public Polygon ingameSettingsArrow;

    @Inject
    public IngameSettingsController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        System.out.println("Controller open");
        ingameSettingOptions.add("MonsterList");
        ingameSettingOptions.add("Monsters");
        ingameSettingOptions.add("Map");
        ingameSettingsListView.setItems(FXCollections.observableArrayList(ingameSettingOptions));

        ingameSettingsListView.setCellFactory(param -> {
            IngameSettingsCell cell = new IngameSettingsCell(this);
            ingameSettingsListView.prefWidthProperty().bind(cell.widthProperty());
            return cell;
        });
        ingameSettingsArrow.setLayoutX(ingameSettingsListView.getWidth() + 2);
        return parent;
    }

    protected void openOption(String option) {
        //TODO: Open the sub menus
        ingameSettingOptions.stream().filter(e -> e.equals(option)).findFirst();
    }
}
