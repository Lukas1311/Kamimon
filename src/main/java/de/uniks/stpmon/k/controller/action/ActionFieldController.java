package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldController extends Controller {
    @FXML
    public StackPane actionFieldPane;
    @FXML
    public Pane actionFieldContent;
    @Inject
    Provider<ActionFieldMainMenuController> mainMenuControllerProvider;
    @Inject
    Provider<ActionFieldChooseAbilityController> chooseAbilityControllerProvider;
    @Inject
    Provider<ActionFieldChangeMonsterController> changeMonsterControllerProvider;
    @Inject
    Provider<ActionFieldBattleLogController> battleLogControllerProvider;
    @Inject
    Provider<ActionFieldChooseOpponentController> chooseOpponentControllerProvider;

    AbilityDto chosenAbility;

    @Inject
    public ActionFieldController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(actionFieldPane, "action_menu_background.png");
        openMainMenu();

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

    public void openMainMenu() {
        open(mainMenuControllerProvider);
    }

    public void openChangeMonster() {
        open(changeMonsterControllerProvider);
    }

    public void openChooseAbility() {
        open(chooseAbilityControllerProvider);
    }

    public void openChooseOpponent() {
        if (chosenAbility != null) {
            open(chooseOpponentControllerProvider);
        }
    }

    public void openBattleLog() {
        open(battleLogControllerProvider);
    }

    private void open(Provider<? extends Controller> provider) {
        actionFieldContent.getChildren().clear();
        actionFieldContent.getChildren().add(provider.get().render());
    }

    public void setChosenAbility(AbilityDto chosenAbility) {
        this.chosenAbility = chosenAbility;
    }

    public AbilityDto getChosenAbility() {
        return chosenAbility;
    }

    public HBox getOptionContainer(String option) {
        Text arrowText = new Text(" >");
        Text optionText = new Text(option);

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, optionText);
        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));

        return optionContainer;
    }
}
