package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldBattleLogController extends Controller {

    @FXML
    public Text logText;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    @Inject
    public ActionFieldBattleLogController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        //TODO: Add text to log


        //TODO: this is just for debugging, remove after real functionality is implemented
        logText.setOnMouseClicked(event -> actionFieldControllerProvider.get().openMainMenu());
        return parent;
    }
    @Override
    public String getResourcePath() {
        return "action/";
    }
}
