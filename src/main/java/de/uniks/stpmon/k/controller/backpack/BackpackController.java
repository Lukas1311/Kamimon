package de.uniks.stpmon.k.controller.backpack;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.service.InputHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class BackpackController extends Controller {

    @FXML
    public ImageView backpackImage;

    @Inject
    public Provider<BackpackMenuController> backpackMenuControllerProvider;

    @Inject
    public Provider<IngameController> ingameControllerProvider;

    @Inject
    public InputHandler inputHandler;

    private Controller backpackMenu;


    @Inject
    public BackpackController() {

    }


    @Override
    public Parent render() {
        Parent render = super.render();
        loadImage(backpackImage, "backpack/backpack.png");
        return render;
    }

    @Override
    public void init() {
        super.init();
        onDestroy(inputHandler.addPressedKeyHandler(event -> {
            if (event.getCode() == KeyCode.B) {
                triggerBackPackMenu();
                event.consume();
            }
        }));
    }

    public void openBackPackMenu() {
        backpackMenu = backpackMenuControllerProvider.get();
        ingameControllerProvider.get().pushController(backpackMenu);
    }

    public void closeBackPackMenu() {
        ingameControllerProvider.get().removeChildren(0);
        backpackMenu = null;
        backpackMenuControllerProvider.get().setAllControllerNull();
    }

    public void triggerBackPackMenu() {
        if (backpackMenu == null) {
            openBackPackMenu();
        } else {
            closeBackPackMenu();
        }
    }

    @Override
    public String getResourcePath() {
        return "backpack/";
    }
}
