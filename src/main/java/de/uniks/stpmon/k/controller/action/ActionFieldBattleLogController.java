package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class ActionFieldBattleLogController extends Controller {

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public VBox vBox;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    @Inject
    SessionService sessionService;

    @Inject
    PresetService presetService;

    @Inject
    EncounterStorage encounterStorage;

    @Inject
    TrainerStorage trainerService;

    @Inject
    MonsterService monsterService;




    private final List<String> texts = new ArrayList<>();

    private final boolean initialized = false;

    @Inject
    public ActionFieldBattleLogController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        scrollPane.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                scrollPane.setVvalue(1.0);
            }
        });

        initListeners();

        Timer myTimer = new Timer();

        onDestroy(myTimer::cancel);
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
            javafx.application.Platform.runLater(() -> {
                vBox.getChildren().clear();
                actionFieldControllerProvider.get().openMainMenu();

            });
            }}, 5000);

        scrollPane.setOnMouseClicked(event -> {
            vBox.getChildren().clear();
            actionFieldControllerProvider.get().openMainMenu();
        });

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
            vBox.getChildren().clear();
            //texts.clear();
            //texts.add(text);
        }
        //else if (texts.size() >= 2) {
        //    texts.remove(0);
        //}
        Label text1 = new Label(text + "\n");
        text1.setWrapText(true);
        text1.setMaxWidth(290);
        vBox.getChildren().add(text1);
        //texts.add(text);
        //renewText();
    }

    private void initListeners(){
        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot).skip(1), opp -> {

                if (encounterStorage.getSession() == null) {
                    return;
                }
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
                        case "target-defeated" -> {
                            //TODO target is the Trainer in R3, in R4 it is the monster
                            addTextSection(translateString("target-defeated", monster.name()), false);
                        }
                        case "monster-changed" -> {
                            //handled above
                        }
                        case "monster-defeated" -> {
                            addTextSection(translateString("monster-defeated", monster.name()), false);
                        }
                        case "monster-levelup" -> {
                            //TODO add level here
                            addTextSection(translateString("monster-levelup", monster.name(), "0"), false);
                            //TODO add Level up handling here?
                        }
                        case "monster-evolved" -> {
                            addTextSection(translateString("monster-evolved", monster.name()), false);
                            //TODO #evolve animation? v4
                        }
                        case "monster-learned" -> {
                            addTextSection(translateString("monster-learned", monster.name(),
                                    presetService.getAbility(result.ability()).blockingFirst().name()), false);
                        }
                        case "monster-dead" -> {
                            addTextSection(translateString("monster-dead", monster.name()), false);
                        }
                        case "ability-unknown" -> {
                            addTextSection(translateString("ability-unknown",
                                            presetService.getAbility(result.ability()).blockingFirst().name(), monster.name())
                                    , false);
                        }
                        case "ability-no-uses" -> {
                            addTextSection(translateString("ability-no-uses",
                                            presetService.getAbility(result.ability()).blockingFirst().name())
                                    , false);
                        }
                        case "target-unknown" -> {
                            addTextSection(translateString("target-unknown"), false);
                        }
                        case "target-dead" -> {
                            //TODO target is the Trainer in R3, in R4 it is the monster
                            addTextSection(translateString("target-dead", monster.name()), false);
                        }
                    }
                }
            });
        }
    }


    private void renewText() {
        //StringBuilder sb = new StringBuilder();
        for (String str : texts) {
            //sb.append(str).append("\n");


        }
        //logText.setText(sb.toString());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
