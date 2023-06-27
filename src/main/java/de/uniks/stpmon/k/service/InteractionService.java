package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.StarterController;
import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
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
            return Dialogue.builder()
                    .addItem(translateString("hello") + ", " + me.name() + "!\n" + translateString("areYouReady"))
                    .addItem(translateString("chooseOneType"))
                    .addItem().setText(translateString("takeTime"))
                    .addOption().setText("Fire")
                    .addSelection(() -> {
                        interactionStorage.selectedStarter().setValue("Fire");
                        starterController.setStarter("1");
                        starterController.starterBox.setVisible(true);
                    })
                    .addAction(() -> {
                        interactionStorage.selectedStarter().reset();
                        starterController.starterBox.setVisible(false);
                        listener.sendTalk(Socket.UDP, "areas.%s.trainers.%s.talked".formatted(trainer.area(), me._id()),
                                new TalkTrainerDto(me._id(), trainer._id(),0));
                    })
                    .setNext(Dialogue.builder().addItem(translateString("chosenFire")).create())
                    .endOption()
                    .addOption().setText("Water")
                    .addSelection(() -> {
                        interactionStorage.selectedStarter().setValue("Water");
                        starterController.setStarter("3");
                        starterController.starterBox.setVisible(true);
                    })
                    .addAction(() -> {
                        interactionStorage.selectedStarter().reset();
                        starterController.starterBox.setVisible(false);
                        listener.sendTalk(Socket.UDP, "areas.%s.trainers.%s.talked".formatted(trainer.area(), me._id()),
                                new TalkTrainerDto(me._id(), trainer._id(),1));
                    })
                    .setNext(Dialogue.builder().addItem(translateString("chosenWater")).create())
                    .endOption()
                    .addOption().setText("Grass")
                    .addSelection(() -> {
                        interactionStorage.selectedStarter().setValue("Grass");
                        starterController.setStarter("5");
                        starterController.starterBox.setVisible(true);
                    })
                    .addAction(() -> {
                        interactionStorage.selectedStarter().reset();
                        starterController.starterBox.setVisible(false);
                        listener.sendTalk(Socket.UDP, "areas.%s.trainers.%s.talked".formatted(trainer.area(), me._id()),
                                new TalkTrainerDto(me._id(), trainer._id(),2));
                    })
                    .setNext(Dialogue.builder().addItem(translateString("chosenGrass")).create())
                    .endOption()
                    .endItem()
                    .create();
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
