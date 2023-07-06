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
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Singleton
public class InteractionService implements ILifecycleService {

    public static final int DISTANCE_CHECKED_FOR_TRAINERS = 2;

    @Inject
    protected Provider<ResourceBundle> resources;
    @Inject
    InteractionStorage interactionStorage;
    @Inject
    TrainerService trainerService;
    @Inject
    PresetService presetService;
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
        Trainer me = trainerService.getMe();

        if (starters != null && !starters.isEmpty() && me.team().isEmpty()) {
            return getStarterDialogue(starters, me, trainer);
        }
        if (info.encounterOnTalk()) {
            return getEncounterDialogue(trainer, me);
        }

        //TODO: Add dialogue for healing
        return null;
    }

    private Dialogue getEncounterDialogue(Trainer trainer, Trainer me) {
        DialogueBuilder itemBuilder = Dialogue.builder()
                .setTrainerId(trainer._id())
                .addItem().setText(translateString("dialogue.intro"))
                .addOption().setText(translateString("dialogue.select.no")).endOption()
                .addOption()
                .setText(translateString("dialogue.select.yes"))
                .addAction(() -> talkTo(trainer, me, null))
                .endOption()
                .endItem();
        return itemBuilder.create();
    }

    private Dialogue getStarterDialogue(List<String> starters, Trainer me, Trainer trainer) {
        DialogueBuilder.ItemBuilder itemBuilder = Dialogue.builder()
                .setTrainerId(trainer._id())
                .addItem(translateString("helloAreYouReady", me.name()))
                .addItem(translateString("chooseOneType"))
                .addItem().setText(translateString("takeTime"));

        for (int select = 0; select < starters.size(); select++) {
            String id = starters.get(select);
            MonsterTypeDto monster = presetService.getMonster(id).blockingFirst();

            int monsterId = monster.id();
            String monsterName = monster.name();
            String monsterType = monster.type().get(0); // starters only have one type

            int finalSelect = select;
            itemBuilder.addOption().setText(monsterType)
                    .addSelection(() -> {
                        interactionStorage.selectedStarter().setValue(monsterName);
                        starterController.setStarter(String.valueOf(monsterId));
                        starterController.starterPane.setVisible(true);
                    })
                    .addAction(() -> {
                        interactionStorage.selectedStarter().reset();
                        starterController.starterPane.setVisible(false);
                        talkTo(trainer, me, finalSelect);
                    })
                    .setNext(Dialogue.builder()
                            .addItem(translateString("youHaveChosenMonster", monsterName, monsterType)).create())
                    .endOption();
        }
        return itemBuilder.endItem().create();
    }

    private void talkTo(Trainer trainer, Trainer me, Integer selection) {
        listener.sendTalk(Socket.UDP, "areas.%s.trainers.%s.talked".formatted(trainer.area(), me._id()),
                new TalkTrainerDto(me._id(), trainer._id(), selection));
    }

    protected String translateString(String word, String... args) {
        String translation = resources.get().getString(word);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i + "}", args[i]);
        }
        return translation;
    }

    /**
     * Retrieves the possible dialogue of a trainer in front of the player.
     *
     * @return The current dialogue, or null if there is none.
     */
    public Dialogue getPossibleDialogue() {
        for (int i = 1; i <= DISTANCE_CHECKED_FOR_TRAINERS; i++) {
            Optional<Trainer> optionalTrainer = trainerService.getFacingTrainer(i);
            if (optionalTrainer.isEmpty()) {
                continue;
            }
            Trainer trainer = optionalTrainer.get();
            Dialogue dialogue = getDialogue(trainer);
            if (dialogue != null) {
                return dialogue;
            }
        }
        return null;
    }

    /**
     * Tries to update the current dialogue to the one of the facing trainer.
     */
    public void tryUpdateDialogue() {
        interactionStorage.setDialogue(getPossibleDialogue());
    }

}
