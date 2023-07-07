package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.SortedMap;
import java.util.TreeMap;

@Singleton
public class ActionFieldChooseAbilityController extends Controller {
    @FXML
    public VBox abilityBox;

    @Inject
    PresetService presetService;
    @Inject
    EncounterStorage encounterStorage;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    public Monster monster;

    @Inject
    public ActionFieldChooseAbilityController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        //monster = encounterStorage.getSession().getMonster(new EncounterSlot(0, false));

        SortedMap<String, Integer> abilities = new TreeMap<>();
        abilities.put("1", 1);
        monster = MonsterBuilder.builder()
                .setAbilities(abilities)
                .create();

        for (String id : monster.abilities().keySet()) {
            addAbility(id);
        }

        return parent;
    }

    public void addAbility(String abilityId) {
        subscribe(presetService.getAbility(abilityId), this::addAbilityOption);
    }

    public void addAbilityOption(AbilityDto ability) {
        Text arrowText = new Text(" >");
        Text ablitiyNameText = new Text(ability.name());
        Text useLabel = new Text(" (??" + "/" + ability.maxUses() + ") ");

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, ablitiyNameText, useLabel);

        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));
        optionContainer.setOnMouseClicked(event -> {
            actionFieldControllerProvider.get().setChosenAbility(ability);
            chooseAbility();
        });

        abilityBox.getChildren().add(optionContainer);

    }

    public void chooseAbility() {
        actionFieldControllerProvider.get().openChooseOpponent();
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
