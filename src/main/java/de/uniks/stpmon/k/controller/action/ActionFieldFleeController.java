package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;

public class ActionFieldFleeController extends Controller {

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    @Inject
    public ActionFieldFleeController(){
    }

    @Override
    public Parent render() {
        Parent parent = super.render();


        return parent;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
