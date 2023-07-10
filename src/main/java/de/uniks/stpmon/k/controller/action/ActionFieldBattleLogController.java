package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.models.EncounterSlot;
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
    Provider<SessionService> sessionServiceProvider;

    @Inject
    Provider<PresetService> presetServiceProvider;


    private List<String> texts = new ArrayList<>();

    @Inject
    public ActionFieldBattleLogController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        for (EncounterSlot slot : sessionServiceProvider.get().getSlots()) {
            subscribe(sessionServiceProvider.get().listenOpponent(slot), opp -> {
                //only when attack
                //if(opp.move() instanceof AbilityMove) {
                    subscribe(presetServiceProvider.get().getMonster(opp.monster()), myMon -> {
                        subscribe(presetServiceProvider.get().getAbility(opp.result().ability().toString()), ability -> {
                            subscribe(presetServiceProvider.get().getMonster(((AbilityMove) opp.move()).target()), eneMon -> {
                                addTextSection(translateString("monsterAttacks",
                                        myMon.name(), eneMon.name(), ability.name()), false);
                            });
                        });
                    });
                //}
            });
        }


        //TODO: this is just for debugging, remove after real functionality is implemented
        logText.setOnMouseClicked(event -> actionFieldControllerProvider.get().openMainMenu());
        return parent;
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
