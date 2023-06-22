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
                    .addItem("hello, welcome to this world!")
                    .addItem("I have a gift for you")
                    .addItem().setText("Choose your starter:")
                    .addOption().setText("Flamingo")
                    .addSelection(() -> interactionStorage.selectedStarter().setValue("Flamingo"))
                    .addAction(() -> interactionStorage.selectedStarter().reset())
                    .endOption()
                    .addOption().setText("Elephant")
                    .addSelection(() -> interactionStorage.selectedStarter().setValue("Elephant"))
                    .addAction(() -> interactionStorage.selectedStarter().reset())
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
}
