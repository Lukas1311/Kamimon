package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Dialogue;
import de.uniks.stpmon.k.models.DialogueOption;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class InteractionService implements ILifecycleService {
    public static final int DISTANCE_CHECKED_FOR_TRAINERS = 2;
    @Inject
    InteractionStorage interactionStorage;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    CacheManager cacheManager;
    private TrainerAreaCache areaCache;

    @Inject
    public InteractionService() {
    }

    public Dialogue getDialogue(Trainer trainer) {
        NPCInfo info = trainer.npc();
        if(info.canHeal()){
            return Dialogue.create(new DialogueOption("Can heal ?"));
        }
        return null;
    }

    public void tryUpdateDialogue() {
        Optional<Trainer> optionalTrainer = getSurroundingTrainers();
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

    public Optional<Trainer> getSurroundingTrainers() {
        if (areaCache == null || areaCache.getStatus() == ICache.Status.DESTROYED) {
            areaCache = cacheManager.trainerAreaCache();
        }
        Trainer trainer = trainerStorage.getTrainer();
        for (int dX = -DISTANCE_CHECKED_FOR_TRAINERS; dX <= DISTANCE_CHECKED_FOR_TRAINERS; dX++) {
            for (int dY = -DISTANCE_CHECKED_FOR_TRAINERS; dY <= DISTANCE_CHECKED_FOR_TRAINERS; dY++) {
                int x = trainer.x() + dX;
                int y = trainer.y() + dY;
                // Upper bounds are not important because cache is designed for  up to 0xFFFF
                Optional<Trainer> trainerOptional = areaCache.getTrainerAt(x, y);
                if (trainerOptional.isPresent()) {
                    return trainerOptional;
                }
            }
        }
        return Optional.empty();
    }
}
