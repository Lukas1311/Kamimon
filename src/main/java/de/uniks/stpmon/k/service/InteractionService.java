package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.service.storage.InteractionStorage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class InteractionService implements ILifecycleService {

    public static final int DISTANCE_CHECKED_FOR_TRAINERS = 2;

    @Inject
    InteractionStorage interactionStorage;
    @Inject
    TrainerService trainerService;

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
        //TODO: Add real dialogue for starters
        if (starters != null && !starters.isEmpty()) {
            return Dialogue.builder()
                    .setTrainerId(trainer._id())
                    .addItem("hello, welcome to this world!")
                    .addItem("I have a gift for you")
                    .addItem().setText("Choose your starter:")
                    .addOption().setText("Flamingo")
                    .addSelection(() -> interactionStorage.selectedStarter().setValue("Flamingo"))
                    .addAction(() -> interactionStorage.selectedStarter().reset())
                    .setNext(Dialogue.builder().addItem("you chose a flamingo!").create())
                    .endOption()
                    .addOption().setText("Elephant")
                    .addSelection(() -> interactionStorage.selectedStarter().setValue("Elephant"))
                    .addAction(() -> interactionStorage.selectedStarter().reset())
                    .setNext(Dialogue.builder().addItem("you chose an elephant!").create())
                    .endOption()
                    .endItem()
                    .create();
        }
        //TODO: Add dialogue for healing
        //TODO: Add dialogue for encounter
        return null;
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
