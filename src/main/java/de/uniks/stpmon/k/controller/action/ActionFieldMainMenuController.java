package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldMainMenuController extends Controller {

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    @FXML
    public Text textContent;
    @FXML
    public VBox mainMenuBox;

    public String fight;
    public String changeMon;
    public String flee;

    @Inject
    public ActionFieldMainMenuController() {}

    @Override
    public Parent render() {
        Parent parent = super.render();

        textContent.setText(translateString("wannaDo"));
        fight = translateString("fight");
        changeMon = translateString("changeMon");
        //TODO: Only show flee in wild encounter. Currently it is always shown to get out of the encounter
        flee = translateString("flee");


        setActions();

        return parent;
    }

    public void setActions() {
        addActionOption(fight);
        addActionOption(changeMon);
        addActionOption(flee);
    }

    public void addActionOption(String option) {
        HBox optionContainer = actionFieldControllerProvider.get().getOptionContainer(option);

        optionContainer.setOnMouseClicked(event -> openAction(option));

        int index = mainMenuBox.getChildren().size();
        optionContainer.getChildren().get(1).setId("main_menu_" + index);

        mainMenuBox.getChildren().add(optionContainer);
    }

    public void openAction(String option) {
        if(option.equals(fight)) {
            openFight();
        } else if (option.equals(changeMon)) {
            openChangeMon();
        }else if(option.equals(flee)){
            IngameController.disableEncounter = true;
            HybridController controller = hybridControllerProvider.get();
            app.show(controller);
            controller.openMain(MainWindow.INGAME);
        }
    }

    public void openFight() {
        actionFieldControllerProvider.get().openChooseAbility();
    }

    public void openChangeMon() {
        actionFieldControllerProvider.get().openChangeMonster();
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
