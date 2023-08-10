package de.uniks.stpmon.k.controller.backpack;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.inventory.InventoryController;
import de.uniks.stpmon.k.controller.mondex.MonDexController;
import de.uniks.stpmon.k.controller.monsters.MonsterBarController;
import de.uniks.stpmon.k.controller.monsters.MonsterInventoryController;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.uniks.stpmon.k.controller.backpack.BackpackMenuOption.*;

@Singleton
public class BackpackMenuController extends Controller {

    private final List<BackpackMenuOption> options = new ArrayList<>();
    private final Map<BackpackMenuOption, Controller> controllers = new HashMap<>();
    @FXML
    public ListView<BackpackMenuOption> backpackMenuListView;

    @FXML
    public HBox backpackMenuHBox;
    @FXML
    public ImageView arrowImageView;

    @Inject
    Provider<MonsterBarController> monsterBarControllerProvider;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    Provider<MonDexController> monDexControllerProvider;
    @Inject
    Provider<MonsterInventoryController> monBoxControllerProvider;
    @Inject
    Provider<InventoryController> inventoryControllerProvider;


    @Inject
    public BackpackMenuController() {

    }

    @Override
    public void init() {
        options.add(MONS);
        options.add(INVENTORY);
        options.add(MONDEX);
        options.add(MAP);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        backpackMenuHBox.setPickOnBounds(false);

        loadBgImage(backpackMenuListView, getResourcePath() + "BackPackMenu_v2.png");
        loadImage(arrowImageView, "arrow_right.png");


        backpackMenuListView.setCellFactory(param -> new BackpackMenuCell(this));

        backpackMenuListView.setItems(FXCollections.observableArrayList(options));


        return parent;
    }


    public void openOption(BackpackMenuOption option) {
        IngameController ingameController = ingameControllerProvider.get();
        if (option == MAP) {
            //close all other controllers
            for (BackpackMenuOption op : controllers.keySet()) {
                if (controllers.get(op) != null) {
                    triggerOption(op);
                }
            }
            ingameController.openOrCloseMap();
        } else {
            if (ingameController.isMapOpen()) {
                ingameController.closeMap();
            }
            triggerOption(option);
        }
    }

    public int getId(BackpackMenuOption option) {
        return options.indexOf(option);
    }

    private Provider<? extends Controller> getProvider(BackpackMenuOption option) {
        Provider<? extends Controller> provider;
        if (option == MONS) {
            provider = monBoxControllerProvider;
        } else if (option == MONDEX) {
            provider = monDexControllerProvider;
        } else {
            provider = inventoryControllerProvider;
        }
        return provider;
    }

    private void openController(BackpackMenuOption option) {
        Provider<? extends Controller> provider = getProvider(option);
        Controller controller = provider.get();
        //set the controller, so the triggerOption methode knows if it needs to be closed
        controllers.put(option, controller);
        ingameControllerProvider.get().pushController(controller);
    }


    private void closeController(BackpackMenuOption option) {
        ingameControllerProvider.get().removeChildren(1);
        controllers.put(option, null);
    }

    private void triggerOption(BackpackMenuOption option) {
        //close all other controllers
        for (BackpackMenuOption boption : options) {
            if (boption == option) {
                continue;
            }
            //check if controller is open
            if (controllers.get(boption) != null) {
                closeController(boption);
            }
        }
        //open/close triggered controller
        Controller controllerToOpen = controllers.get(option);
        if (controllerToOpen == null) {
            openController(option);
        } else {
            closeController(option);
        }
    }

    public void closeAll() {
        for (BackpackMenuOption op : controllers.keySet()) {
            if (controllers.get(op) != null) {
                triggerOption(op);
            }
        }
    }

    public void setAllControllerNull() {
        for (BackpackMenuOption option : options) {
            controllers.put(option, null);
        }
        options.clear();
    }

    @Override
    public String getResourcePath() {
        return "backpack/";
    }
}
