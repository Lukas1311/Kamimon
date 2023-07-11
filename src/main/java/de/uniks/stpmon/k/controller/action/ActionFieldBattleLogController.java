package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Result;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ActionFieldBattleLogController extends Controller {

    @FXML
    public Text logText;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    @Inject
    SessionService sessionService;

    @Inject
    PresetService presetService;


    private final List<String> texts = new ArrayList<>();

    @Inject
    public ActionFieldBattleLogController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot), opp -> {
                for (Result result : opp.results()) {
                    if (!result.type().equals("ability-success")) {
                        continue;
                    }
                    //only when attack
                    //if(opp.move() instanceof AbilityMove) {
                    MonsterTypeDto myMon = getTypeForSlot(slot);
                    // Blocking can be used here because values are already loaded in the cache
                    AbilityDto ability = presetService.getAbility(result.ability()).blockingFirst();
//                    EncounterSlot targetSlot = sessionService.getTarget(((AbilityMove) opp.move()).target());
//                    MonsterTypeDto eneMon = getTypeForSlot(targetSlot);
//                    addTextSection(translateString("monsterAttacks", myMon.name(), eneMon.name(), ability.name()),
//                            false);
                    //}
                }
            });
        }


        //TODO: this is just for debugging, remove after real functionality is implemented
        logText.setOnMouseClicked(event -> actionFieldControllerProvider.get().openMainMenu());
        return parent;
    }

    private MonsterTypeDto getTypeForSlot(EncounterSlot slot) {
        Monster monster = sessionService.getMonster(slot);
        // Blocking can be used here because values are already loaded in the cache
        return presetService.getMonster(monster.type()).blockingFirst();
    }


    public void addTextSection(String text, boolean overwriteAll) {
        //replace whole text in battle log
        if (overwriteAll) {
            texts.clear();
            texts.add(text);

        } else if (texts.size() >= 2) {
            texts.remove(0);
        }

        texts.add(text);
        renewText();
    }


    private void renewText() {
        if (texts != null) {
            StringBuilder sb = new StringBuilder();
            for (String str : texts) {
                sb.append(str).append("\n");
            }
            logText.setText(sb.toString());
        }
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
