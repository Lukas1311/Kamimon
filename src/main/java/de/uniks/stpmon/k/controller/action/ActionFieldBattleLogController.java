package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
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
import java.util.Timer;
import java.util.TimerTask;

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
                MonsterTypeDto monster = getTypeForSlot(slot);

                if (opp.move() instanceof AbilityMove move) {
                    // Blocking can be used here because values are already loaded in the cache
                    AbilityDto ability = presetService.getAbility(move.ability()).blockingFirst();
                    EncounterSlot targetSlot = sessionService.getTarget(((AbilityMove) opp.move()).target());
                    MonsterTypeDto eneMon = getTypeForSlot(targetSlot);
                    addTextSection(translateString("monsterAttacks", monster.name(), eneMon.name(), ability.name()), false);
                    return;
                }

                if (opp.move() instanceof ChangeMonsterMove move) {
                    addTextSection(translateString("monster-changed",
                            presetService.getMonster(move.monster()).blockingFirst().name()), false);
                    return;
                }

                for (Result result : opp.results()) {
                    switch (result.type()) {
                        case "ability-success" -> {
                            String translationVar = switch (result.effectiveness()) {
                                case "super-effective" -> "super-effective-atk";
                                case "effective" -> "effective-atk";
                                case "normal" -> "normal-atk";
                                case "ineffective" -> "ineffective-atk";
                                case "no-effect" -> "no-effect-atk";
                                default -> "";
                            };
                            addTextSection(translateString(translationVar), false);
                        }

                        case "target-defeated" -> //TODO target is the Trainer in R3, in R4 it is the monster
                                addTextSection(translateString("target-defeated", monster.name()), false);

                        case "monster-changed" -> {
                            //handled above
                        }

                        case "monster-defeated" ->
                                addTextSection(translateString("monster-defeated", monster.name()), false);

                        case "monster-levelup" -> //TODO add level here
                            //TODO add Level up handling here?
                                addTextSection(translateString("monster-levelup", monster.name(), "0"), false);

                        case "monster-evolved" -> //TODO #evolve animation? v4
                                addTextSection(translateString("monster-evolved", monster.name()), false);

                        case "monster-learned" -> addTextSection(translateString("monster-learned", monster.name(),
                                presetService.getAbility(result.ability()).blockingFirst().name()), false);

                        case "monster-dead" -> addTextSection(translateString("monster-dead", monster.name()), false);

                        case "ability-unknown" -> addTextSection(translateString("ability-unknown",
                                presetService.getAbility(result.ability()).blockingFirst().name(), monster.name()), false);

                        case "ability-no-uses" -> addTextSection(translateString("ability-no-uses",
                                presetService.getAbility(result.ability()).blockingFirst().name()), false);

                        case "target-unknown" -> addTextSection(translateString("target-unknown"), false);

                        case "target-dead" -> //TODO target is the Trainer in R3, in R4 it is the monster
                                addTextSection(translateString("target-dead", monster.name()), false);
                    }
                }
            });
        }


        Timer myTimer = new Timer();

        onDestroy(myTimer::cancel);
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> actionFieldControllerProvider.get().openMainMenu());
            }
        }, 3000);

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
        StringBuilder sb = new StringBuilder();
        for (String str : texts) {
            sb.append(str).append("\n");
        }
        logText.setText(sb.toString());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
