package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InteractionServiceTest {

    @Spy
    InteractionStorage interactionStorage;
    @Mock
    TrainerService trainerService;
    @InjectMocks
    InteractionService interactionService;

    @Test
    void emptyDialogue() {
        // Empty at first
        when(trainerService.getFacingTrainer()).thenReturn(Optional.empty());

        // Search for dialogue in facing trainer
        interactionService.tryUpdateDialogue();
        // No dialogue found, still empty
        assertNull(interactionStorage.getDialogue());
    }

    @Test
    void noDialogue() {
        // Empty at first
        assertNull(interactionStorage.getDialogue());

        when(trainerService.getFacingTrainer()).thenReturn(Optional.of(DummyConstants.TRAINER));

        // Search for dialogue in facing trainer
        interactionService.tryUpdateDialogue();
        // Not found dialogue, still empty
        assertNull(interactionStorage.getDialogue());
    }

    @Test
    void monsterDialogue() {
        // Empty at first
        assertNull(interactionStorage.getDialogue());

        Trainer trainer = TrainerBuilder.builder()
                .setNpc(new NPCInfo(false,
                        false,
                        false,
                        List.of("monster_0", "monster_1"),
                        List.of()))
                .create();
        when(trainerService.getFacingTrainer()).thenReturn(Optional.of(trainer));

        // Search for dialogue in facing trainer
        interactionService.tryUpdateDialogue();
        // Found dialogue
        Dialogue dialogue = interactionStorage.getDialogue();
        assertNotNull(dialogue);
    }
}
