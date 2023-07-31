package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ActionFieldChooseOpponentController extends BaseActionFieldController {
    @FXML
    public Text chooseOpponentText;
    @FXML
    public HBox chooseOpponentBox;
    private int optionIndex = 0;

    @Inject
    public ActionFieldChooseOpponentController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        optionIndex = 0;
        addMonsterOption(null, null, true);

        for (EncounterSlot slot : sessionService.getSlots()) {
            if (!slot.enemy()) {
                continue;
            }
            // Skip if the monster is dead
            if (sessionService.isMonsterDead(slot)) {
                // But add it if it is revived or switched
                subscribe(sessionService.listenMonster(slot).skip(1), (monster) -> {
                    if (!sessionService.isMonsterDead(slot)) {
                        addMonsterForSlot(slot);
                    }
                });
                continue;
            }
            addMonsterForSlot(slot);
        }

        return parent;
    }

    private void addMonsterForSlot(EncounterSlot slot) {
        Opponent opponent = sessionService.getOpponent(slot);
        Monster oppMonster = sessionService.getMonster(slot);

        subscribe(presetService.getMonster(oppMonster.type()),
                monsterDto -> addMonsterOption(opponent, monsterDto.name(), false));
    }

    public void addMonsterOption(Opponent opponent, String monsterName, boolean isBackOption) {
        ActionFieldController actionField = getActionField();
        HBox optionContainer = ActionFieldController
                .getOptionContainer(isBackOption ? translateString("back") : monsterName);

        optionContainer.setOnMouseClicked(event -> {
            if (isBackOption) {
                actionField.openChooseAbility();
            } else {
                actionField.selectEnemy(opponent);
            }
        });

        // each column containing a maximum of 2 options
        int index = optionIndex / 2;
        if (chooseOpponentBox.getChildren().size() <= index) {
            VBox vbox = new VBox();
            chooseOpponentBox.getChildren().add(vbox);
        }
        VBox vbox = (VBox) chooseOpponentBox.getChildren().get(index);

        // set IDs for the options
        int optionIndex = vbox.getChildren().size();
        optionContainer.setId("user_monster_" + (index * 2 + optionIndex));

        // if the option is 'Back', add it to the end of the VBox
        if (isBackOption) {
            vbox.getChildren().add(optionContainer);
        } else {
            vbox.getChildren().add(0, optionContainer);
        }

        this.optionIndex++;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
