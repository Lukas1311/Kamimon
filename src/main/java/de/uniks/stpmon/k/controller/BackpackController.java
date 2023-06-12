package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Provider;

public class BackpackController extends Controller {

    @FXML
    public ImageView backpackImage;

    @Inject
    Provider<BackpackMenuController> backpackMenuControllerProvider;

    @Inject
    Provider<IngameController> ingameControllerProvider;

    HBox backpackMenu;


    @Inject
    public BackpackController() {

    }


    @Override
    public Parent render() {
        Parent parent = super.render();

        backpackImage.setOnMouseClicked(click -> {
            if (backpackMenu == null) {
                backpackMenu = (HBox) backpackMenuControllerProvider.get().render();

                ingameControllerProvider.get().addBackpackMenu(backpackMenu);
            } else {
                ingameControllerProvider.get().removeBackpackMenu(backpackMenu);
                backpackMenu = null;

            }
        });
        return parent;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();

    }

    @Override
    public void onDestroy(Runnable action) {
        super.onDestroy(action);
    }

    public void openBackPackMenu(MouseEvent mouseEvent) {

    }
}
