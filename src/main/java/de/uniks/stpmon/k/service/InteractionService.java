package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.StarterController;
import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.models.dialogue.DialogueBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.InteractionStorage;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    @Inject
    public InteractionService() {
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

        if (starters != null && !starters.isEmpty()) {
            Trainer me = trainerService.getMe();

            DialogueBuilder.ItemBuilder itemBuilder = Dialogue.builder().addItem(translateString("hello") + ", " + me.name() + "!\n" + translateString("areYouReady"))
                    .addItem(translateString("chooseOneType"))
                    .addItem().setText(translateString("takeTime"));

            int index = 0;

            for (String starter : starters) {
                int starterIndex = index;

                String monsterType;
                String monsterName;

                switch (starter) {
                    case "1" -> {
                        monsterType = "Fire";
                        monsterName = "Flamander";
                    }
                    case "3" -> {
                        monsterType = "Water";
                        monsterName = "Octi";
                    }
                    case "5" -> {
                        monsterType = "Grass";
                        monsterName = "Caterpi";
                    }
                    default -> {
                        monsterType = "";
                        monsterName = "";
                    }
                }

                itemBuilder.addOption().setText(monsterType)
                        .addSelection(() -> {
                            interactionStorage.selectedStarter().setValue(starter);
                            starterController.setStarter(starter);
                            starterController.starterPane.setVisible(true);
                        })
                        .addAction(() -> {
                            interactionStorage.selectedStarter().reset();
                            starterController.starterPane.setVisible(false);
                            listener.sendTalk(Socket.UDP, "areas.%s.trainers.%s.talked".formatted(trainer.area(), me._id()),
                                    new TalkTrainerDto(me._id(), trainer._id(), starterIndex));
                        })
                        .setNext(Dialogue.builder()
                                .addItem(translateString("chosen") + " " + monsterName + ", " + translateString("the") + " " + monsterType + " " + translateString("monster")).create())
                        .endOption();

                index++;
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
