package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.di.HttpModule_AuthApiFactory;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;

public class ActionFieldFleeController extends Controller {

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;


    @Inject
    public ActionFieldFleeController(){
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        IngameController.disableEncounter = true;
        HybridController controller = hybridControllerProvider.get();
        app.show(controller);
        controller.openMain(MainWindow.INGAME);

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
