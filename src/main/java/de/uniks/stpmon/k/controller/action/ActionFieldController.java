package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.EncounterService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
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
    @Inject
    Provider<ActionFieldFleeController> fleeControllerProvider;


    @Inject
    Provider<EncounterStorage> encounterStorageProvider;

    @Inject
    Provider<EncounterService> encounterServiceProvider;

    private String enemyTrainerId;
    private int abilityId;

    private Controller controller;

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

    public void openMainMenu(){
        open(mainMenuControllerProvider);
    }

    public void openChangeMonster(){
        open(changeMonsterControllerProvider);
    }

    public void openChooseAbility(){
        open(chooseAbilityControllerProvider);
    }

    public void openFleeWildMonster() {
        open(fleeControllerProvider);
    }

    public void openChooseOpponent(){
        open(chooseOpponentControllerProvider);
    }

    public void openBattleLog(){
        open(battleLogControllerProvider);
    }

    private void open(Provider<? extends Controller> provider){
        if(controller != null && controller instanceof ActionFieldBattleLogController){
            controller.destroy();
        }
        actionFieldContent.getChildren().clear();
        controller = provider.get();
        controller.init();
        actionFieldContent.getChildren().add(controller.render());
    }


    public HBox getOptionContainer(String option){
        Text arrowText = new Text(" >");

        Text optionText = new Text(option);

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, optionText);
        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));

        return optionContainer;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    @SuppressWarnings("unused")
    public int getAbilityId() {
        return abilityId;
    }

    public void setEnemyTrainerId(String trainerId) {
        this.enemyTrainerId = trainerId;
    }

    @SuppressWarnings("unused")
    public String getEnemyTrainerId() {
        return enemyTrainerId;
    }

    public void executeAbilityMove(){
        subscribe(encounterServiceProvider.get().makeAbilityMove(abilityId, enemyTrainerId),
                next -> {},
                error -> {
                    //TODO removed if fixed

                    //HttpException err = (HttpException) error;
                    //String text = new String(err.response().errorBody().bytes(), StandardCharsets.UTF_8);
                    ////String text2 = new String(err.response().raw().request().body()., StandardCharsets.UTF_8);
                    //System.out.println(text);
                    System.out.println(error.getMessage());
                }
        );

    }

}
