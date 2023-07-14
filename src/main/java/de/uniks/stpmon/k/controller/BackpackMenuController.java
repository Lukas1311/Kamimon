package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.BackpackMenuCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

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
    public HBox backpackMenuHBox;
    @FXML
    public ImageView arrowImageView;


    @Inject
    BackpackController backpackController;
    @Inject
    Provider<MonsterBarController> monsterBarControllerProvider;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    Provider<MonBoxController> monBoxControllerProvider;

    @Inject
    public BackpackMenuController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        backpackMenuHBox.setPickOnBounds(false);

        loadBgImage(backpackMenuListView, "backpackMenuBox.png");
        loadImage(arrowImageView, "arrow_right.png");

        if (backpackMenuOptions.isEmpty()) {
            backpackMenuOptions.add(MONSTER);
            backpackMenuOptions.add(TEAM);
            backpackMenuOptions.add(MAP);
        }
        backpackMenuListView.setCellFactory(param -> new BackpackMenuCell(this));

        backpackMenuListView.setItems(FXCollections.observableArrayList(backpackMenuOptions));


        return parent;
    }


    public void openOption(BackpackMenuOption option) {
        switch (option) {
            // delete dummy method after functionality is implemented
            case MONSTER -> triggerMonBox();
            case TEAM -> monsterBarControllerProvider.get().showMonsters();
            case MAP -> openMinimap();
        }

    }

    public int getId(BackpackMenuOption option) {
        return backpackMenuOptions.indexOf(option);
    }

    private void openMinimap() {
        ingameControllerProvider.get().openMap();
    }

    public void openMonBox() {
        monBox = monBoxControllerProvider.get();
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

    public void setMonBoxNull() {
        monBox = null;
    }
}
