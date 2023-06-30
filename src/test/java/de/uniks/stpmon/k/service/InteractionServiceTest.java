package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InteractionServiceTest {

    @Spy
    InteractionStorage interactionStorage;
    @Mock
    TrainerService trainerService;
    @Mock
    PresetService presetService;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @InjectMocks
    InteractionService interactionService;

    @Test
    void emptyDialogue() {
        // Empty at first
        when(trainerService.getFacingTrainer(1)).thenReturn(Optional.empty());

        // Search for dialogue in facing trainer
        interactionService.tryUpdateDialogue();
        // No dialogue found, still empty
        assertNull(interactionStorage.getDialogue());
    }

    @Test
    void noDialogue() {
        // Empty at first
        assertNull(interactionStorage.getDialogue());

        when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(DummyConstants.TRAINER));

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
        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", null, Arrays.asList("type1", "type2"), null);

        when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(trainer));
        when(trainerService.getMe()).thenReturn(DummyConstants.TRAINER);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));

        // Search for dialogue in facing trainer
        interactionService.tryUpdateDialogue();
        // Found dialogue
        Dialogue dialogue = interactionStorage.getDialogue();
        assertNotNull(dialogue);
    }

    @Test
    void checkPossibleDialogue() {
        // Mock values
        Trainer firstTrainer = TrainerBuilder.builder()
                .setId("first")
                .setNpc(new NPCInfo(false,
                        false,
                        false,
                        List.of("monster_0", "monster_1"),
                        List.of()))
                .create();
        Trainer secondTrainer = TrainerBuilder.builder(firstTrainer)
                .setId("second")
                .create();
        // Trainer with no dialogue
        Trainer dummyTrainer = TrainerBuilder.builder()
                .setId("dummy")
                .setNpc(new NPCInfo(false,
                        false,
                        false,
                        List.of(),
                        List.of()))
                .create();
        // First no trainer returned, no dialogue should be found
        when(trainerService.getFacingTrainer(2)).thenReturn(Optional.empty());
        when(trainerService.getFacingTrainer(1)).thenReturn(Optional.empty());

        Dialogue firstDialogue = interactionService.getPossibleDialogue();
        // No dialogue should be found
        assertNull(firstDialogue);

        // Now second trainer should be found
        when(trainerService.getFacingTrainer(2)).thenReturn(Optional.of(secondTrainer));

        Dialogue secondDialogue = interactionService.getPossibleDialogue();
        // Trainer should now be found
        assertNotNull(secondDialogue);
        assertEquals("second", secondDialogue.getTrainerId());

        // Add dummy trainer should not be found
        when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(dummyTrainer));

        Dialogue thirdDialogue = interactionService.getPossibleDialogue();
        // Should still use second trainer, dummy should have no dialogue
        assertNotNull(thirdDialogue);
        assertEquals("second", thirdDialogue.getTrainerId());

        // Now first trainer should be found
        when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(firstTrainer));

        Dialogue fourthDialogue = interactionService.getPossibleDialogue();
        // Should now use dialogue from first trainer
        assertNotNull(fourthDialogue);
        assertEquals("first", fourthDialogue.getTrainerId());
    }
}
