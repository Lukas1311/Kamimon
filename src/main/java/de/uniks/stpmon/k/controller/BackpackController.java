package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class BackpackController extends Controller {

    @FXML
    public ImageView backpackImage;
    @FXML
    HBox backpackMenu;

    @Inject
    Provider<BackpackMenuController> backpackMenuControllerProvider;

    @Inject
    Provider<IngameController> ingameControllerProvider;




    @Inject
    public BackpackController() {

    }


    @Override
    public Parent render() {
        return super.render();
    }

    @Override
    public void init() {
        super.init();
    }

    public void openBackPackMenu() {
        backpackMenu = (HBox) backpackMenuControllerProvider.get().render();
        ingameControllerProvider.get().addBackpackMenu(backpackMenu);
    }

    public void closeBackPackMenu() {
        ingameControllerProvider.get().removeBackpackMenu(backpackMenu);
        backpackMenu = null;
    }


    public void triggerBackPackMenu() {
        if (backpackMenu == null) {
            openBackPackMenu();
        } else {
            closeBackPackMenu();
        }
    }
}
