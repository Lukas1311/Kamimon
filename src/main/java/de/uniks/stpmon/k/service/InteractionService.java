package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.StarterController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.models.dialogue.DialogueBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class InteractionService implements ILifecycleService {
    @Inject
    protected Provider<ResourceBundle> resources;
    @Inject
    InteractionStorage interactionStorage;
    @Inject
    TrainerService trainerService;
    @Inject
    PresetService presetService;
    @Inject
    MonsterService monsterService;
    @Inject
    StarterController starterController;
    @Inject
    EventListener listener;

    protected CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public InteractionService() {
    }

    public void destroy() {
        disposables.dispose();
        disposables = new CompositeDisposable();
    }

    public Dialogue getDialogue(Trainer trainer) {
        NPCInfo info = trainer.npc();
        if (info == null) {
            return null;
        }
        if (info.canHeal()) {
            return Dialogue.builder().addItem("Can heal ?").create();
        }

        List<String> starters = info.starters();


        if (starters != null && starters.size() == 3) {
            Map<Integer, MonsterTypeDto> startersMap = new HashMap<>();

            disposables.add(
                    Observable.range(0, starters.size())
                            .flatMap(index -> presetService.getMonster(starters.get(index))
                                    .map(monster -> {
                                        startersMap.put(index, monster);
                                        return monster;
                                    })
                            )
                            .subscribe(
                                    monster -> {}, // startersMap.put(monster.name(), monster.type().get(0)),
                                    Throwable::printStackTrace
                            )
            );

            Trainer me = trainerService.getMe();

            DialogueBuilder.ItemBuilder itemBuilder = Dialogue.builder().addItem(translateString("helloAreYouReady", me.name()))
                    .addItem(translateString("chooseOneType"))
                    .addItem().setText(translateString("takeTime"));

            for (Map.Entry<Integer, MonsterTypeDto> entry : startersMap.entrySet()) {

                int index = entry.getKey();
                int monsterId = entry.getValue().id();
                String monsterName = entry.getValue().name();
                String monsterType = entry.getValue().type().get(0); // starters only have one type

                itemBuilder.addOption().setText(monsterType)
                        .addSelection(() -> {
                            interactionStorage.selectedStarter().setValue(monsterName);
                            starterController.setStarter(String.valueOf(monsterId));
                            starterController.starterPane.setVisible(true);
                        })
                        .addAction(() -> {
                            interactionStorage.selectedStarter().reset();
                            starterController.starterPane.setVisible(false);
                            listener.sendTalk(Socket.UDP, "areas.%s.trainers.%s.talked".formatted(trainer.area(), me._id()),
                                    new TalkTrainerDto(me._id(), trainer._id(), index));
                        })
                        .setNext(Dialogue.builder()
                                .addItem(translateString("youHaveChosenMonster", monsterName, monsterType)).create())
                        .endOption();
            }
            return itemBuilder.endItem().create();
        }


        //TODO: Add dialogue for healing
        //TODO: Add dialogue for encounter
        return null;
    }

    public void tryUpdateDialogue() {
        Optional<Trainer> optionalTrainer = trainerService.getFacingTrainer();
        if (optionalTrainer.isEmpty()) {
            interactionStorage.setDialogue(null);
            return;
        }
        Trainer trainer = optionalTrainer.get();
        Dialogue dialogue = getDialogue(trainer);
        if (dialogue == null) {
            return;
        }
        interactionStorage.setDialogue(dialogue);
    }

    protected String translateString(String word, String... args) {
        String translation = resources.get().getString(word);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i + "}", args[i]);
        }
        return translation;
    }
}
