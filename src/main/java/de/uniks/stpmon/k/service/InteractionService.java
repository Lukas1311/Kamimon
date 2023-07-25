package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.IngameController;
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
    @Inject
    Provider<IngameController> ingameControllerProvider;

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
        Trainer me = trainerService.getMe();

        // no npc
        if (info == null) {
            return null;
        }

        // if npc is healer
        if (info.canHeal()) {
            return getHealDialogue(trainer, me);
        }

        // if npc distributes starter mons
        List<String> starters = info.starters();
        if (starters != null && !starters.isEmpty() && !info.encountered().contains(me._id())) {
            return getStarterDialogue(starters, me, trainer);
        }

        // if encounter starts with interaction
        if (info.encounterOnTalk()) {
            return getEncounterDialogue(trainer, me);
        }

        // if npc trades mons
        List<Integer> availableItems = info.sells();
        if (availableItems != null && !availableItems.isEmpty()) {
            return getTradeDialogue(trainer);
        }

        return null;
    }

    private Dialogue getEncounterDialogue(Trainer trainer, Trainer me) {
        DialogueBuilder itemBuilder = Dialogue.builder()
                .setTrainerId(trainer._id())
                .addItem().setText(translateString("dialogue.intro"))
                .addOption()
                .setText(translateString("dialogue.select.no")).endOption()
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

    private Dialogue getHealDialogue(Trainer trainer, Trainer me) {
        DialogueBuilder itemBuilder = Dialogue.builder()
                .setTrainerId(trainer._id())
                .addItem().setText(translateString("heal.intro"))
                .addOption()
                .setText(translateString("yes"))
                .addAction(this::applyOverlayEffect)
                .setNext(Dialogue.builder()
                        .addItem().setText("... ... ...")
                        .addAction(() -> talkTo(trainer, me, null))
                        .endItem()
                        .addItem().setText(translateString("dialogue.healed"))
                        .endItem()
                        .create())
                .endOption()
                .addOption().setText(translateString("no"))
                .endOption()
                .endItem();

        return itemBuilder.create();
    }

    private Dialogue getTradeDialogue(Trainer trainer) {
        DialogueBuilder itemBuilder = Dialogue.builder()
                .setTrainerId(trainer._id())
                .addItem().setText(translateString("shop.intro"))
                .addOption()
                .setText(translateString("shop.selection.no")).endOption()
                .addOption()
                .setText(translateString("shop.selection.yes"))
                .addAction(() -> openShop(trainer))
                .endOption()
                .endItem();
        return itemBuilder.create();
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

    private void applyOverlayEffect() {
        ingameControllerProvider.get().applyHealEffect();
    }



    /**
     * Open the shop controller to buy things
     */
    private void openShop(Trainer npc){
        System.out.println("NPC sells following items:");
        for (Integer item : npc.npc().sells()) {
            System.out.println(presetService.getItem(item.toString()).blockingFirst().name());
        }
        System.out.println();

        ingameControllerProvider.get().openShop(npc);




    }
}