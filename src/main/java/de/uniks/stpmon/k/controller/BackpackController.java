package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.encounter.LoadingWildEncounterController;
import de.uniks.stpmon.k.service.InputHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.PrimitiveIterator;

@Singleton
public class BackpackController extends Controller {

    @FXML
    public ImageView backpackImage;

    private Controller backpackMenu;

    private Controller loadingWildEncounter;

    private Controller loadingEncounter;

    @Inject
    Provider<LoadingWildEncounterController> loadingWildEncounterControllerProvider;

    @Inject
    Provider<BackpackMenuController> backpackMenuControllerProvider;

    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    Provider<MonBoxController> monBoxControllerProvider;
    @Inject
    Provider<MonsterInformationController> monsterInformationControllerProvider;

    @Inject
    InputHandler inputHandler;


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
        onDestroy(inputHandler.addPressedKeyHandler(event -> {
            if (event.getCode() == KeyCode.B) {
                triggerBackPackMenu();
                event.consume();
            }
        }));
    }

    public void openBackPackMenu() {
        loadingWildEncounter = loadingWildEncounterControllerProvider.get();
        ingameControllerProvider.get().pushController(loadingWildEncounter);
        //backpackMenu = backpackMenuControllerProvider.get();
        //ingameControllerProvider.get().pushController(backpackMenu);
    }

    public void closeBackPackMenu() {
        ingameControllerProvider.get().removeChildren(0);
        backpackMenu = null;
        backpackMenuControllerProvider.get().setMonBoxNull();
    }


    public void triggerBackPackMenu() {
        if (backpackMenu == null) {
            openBackPackMenu();
        } else {
            closeBackPackMenu();
        }
    }
}
