package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class BackpackController extends Controller {

    @FXML
    public ImageView backpackImage;

    private Controller backpackMenu;

    @Inject
    Provider<BackpackMenuController> backpackMenuControllerProvider;

    @Inject
    Provider<IngameController> ingameControllerProvider;


    @Inject
    public BackpackController() {

    }


    @Override
    public Parent render() {
        Parent render = super.render();
        loadImage(backpackImage, "backpack.png");
        return render;
    }

    @Override
    public void init() {
        super.init();
    }

    public void openBackPackMenu() {
        backpackMenu = backpackMenuControllerProvider.get();
        ingameControllerProvider.get().pushController(backpackMenu);
    }

    public void closeBackPackMenu() {
        ingameControllerProvider.get().removeChildren(0);
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
