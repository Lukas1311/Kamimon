package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.di.HttpModule_AuthApiFactory;
import de.uniks.stpmon.k.service.EncounterService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;

public class ActionFieldFleeController extends Controller {

    @FXML
    ImageView blackScreen;
    @FXML
    VBox vbox;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    EncounterService encounterService;
    @Inject
    EncounterStorage encounterStorage;


    @Inject
    public ActionFieldFleeController(){
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        fleeFromWildEncounter();

        return parent;
    }

    private void fleeFromWildEncounter() {
        //IngameController.disableEncounter = true;


        HybridController controller = hybridControllerProvider.get();
        app.show(controller);
        controller.openMain(MainWindow.INGAME);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
